plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's build.gradle
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.paparazzi) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.buildconfig) apply false
    alias(libs.plugins.testRetry) apply false
}

tasks.register("runFlakyReporter") {
    group = "verification"
    description = "Parses test results and generates a flaky test report."

    doLast {
        val resultsDir = file("composeApp/build/test-results")
        val historyFile = file("flaky-tests.json")
        val reportFile = file("flaky-report.md")

        if (!resultsDir.exists()) {
            println("✅ No test results directory found at ${resultsDir.absolutePath}")
            return@doLast
        }

        val testResults = mutableMapOf<String, TestStats>()
        
        resultsDir.walkTopDown()
            .filter { it.extension == "xml" }
            .forEach { file ->
                val doc = groovy.xml.XmlParser().parse(file)
                doc.children().filter { (it as groovy.util.Node).name() == "testcase" }.forEach { 
                    val testCase = it as groovy.util.Node
                    val className = testCase.attribute("classname") as String
                    val methodName = testCase.attribute("name") as String
                    val fullName = "$className#$methodName"
                    
                    val stats = testResults.getOrPut(fullName) { TestStats() }
                    val hasFailure = testCase.children().any { 
                        val child = it as groovy.util.Node
                        child.name() == "failure" || child.name() == "error"
                    }
                    
                    if (hasFailure) {
                        stats.failures++
                    } else {
                        stats.passes++
                    }
                }
            }

        if (testResults.isEmpty()) {
            println("✅ No test results found.")
            return@doLast
        }

        val history = loadHistory(historyFile)
        val newHistory = history.toMutableMap()

        val flakyTests = testResults.filter { it.value.isFlaky() }
        val failedTests = testResults.filter { it.value.isFailed() }
        
        println("\n📊 Test Summary:")
        println("Total Tests: ${testResults.size}")
        println("Failed Tests: ${failedTests.size}")
        println("Flaky Tests: ${flakyTests.size}")

        flakyTests.forEach { (name, stats) ->
            val record = newHistory.getOrDefault(name, FlakyRecord(0, ""))
            newHistory[name] = FlakyRecord(record.flakyCount + 1, java.time.Instant.now().toString())
            println("⚠️ Flaky: $name (${newHistory[name]?.flakyCount} times total)")
        }

        saveHistory(historyFile, newHistory)
        generateReport(reportFile, testResults.size, failedTests, flakyTests, newHistory)
    }
}

class TestStats(var passes: Int = 0, var failures: Int = 0) {
    fun isFlaky() = failures > 0 && passes > 0
    fun isFailed() = failures > 0 && passes == 0
}

class FlakyRecord(val flakyCount: Int, val lastSeen: String)

fun loadHistory(file: File): Map<String, FlakyRecord> {
    if (!file.exists()) return emptyMap()
    val history = mutableMapOf<String, FlakyRecord>()
    try {
        val json = groovy.json.JsonSlurper().parse(file) as Map<String, Map<String, Any>>
        json.forEach { (key, value) ->
            history[key] = FlakyRecord(value["flakyCount"] as Int, value["lastSeen"] as String)
        }
    } catch (e: Exception) {
        println("⚠️ Could not load history: ${e.message}")
    }
    return history
}

fun saveHistory(file: File, history: Map<String, FlakyRecord>) {
    val sortedHistory = history.entries.sortedByDescending { it.value.flakyCount }
        .associate { it.key to mapOf("flakyCount" to it.value.flakyCount, "lastSeen" to it.value.lastSeen) }
    file.writeText(groovy.json.JsonBuilder(sortedHistory).toPrettyString())
}

fun generateReport(file: File, total: Int, failed: Map<String, TestStats>, flaky: Map<String, TestStats>, history: Map<String, FlakyRecord>) {
    val report = StringBuilder("# 🧪 CI Flaky Test Report\n\n")
    report.append("Generated on: ${java.time.Instant.now()}\n\n")
    
    report.append("## 📊 Summary\n\n")
    report.append("- **Total Tests:** $total\n")
    report.append("- **Failed Tests:** ${failed.size}\n")
    report.append("- **Flaky Tests:** ${flaky.size}\n\n")

    if (flaky.isNotEmpty()) {
        report.append("## ⚠️ Flaky Tests Detected in this Run\n\n")
        report.append("| Test Name | Total Flaky Occurrences |\n")
        report.append("| :--- | :---: |\n")
        flaky.keys.sorted().forEach { name ->
            report.append("| `$name` | ${history[name]?.flakyCount ?: 1} |\n")
        }
        report.append("\n")
    }

    if (failed.isNotEmpty()) {
        report.append("## ❌ Failed Tests\n\n")
        failed.keys.sorted().forEach { name ->
            report.append("- `$name`\n")
        }
        report.append("\n")
    }

    val topFlaky = history.entries.sortedByDescending { it.value.flakyCount }.take(10)
    if (topFlaky.isNotEmpty()) {
        report.append("## 🏆 Top Flaky Tests (All Time)\n\n")
        report.append("| Test Name | Flaky Count | Last Seen |\n")
        report.append("| :--- | :---: | :--- |\n")
        topFlaky.forEach { (name, record) ->
            report.append("| `$name` | ${record.flakyCount} | ${record.lastSeen.substringBefore("T")} |\n")
        }
    }

    file.writeText(report.toString())
}
