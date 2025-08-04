#!/bin/bash

# Local test execution with API Coverage integration
echo "🚀 Starting local tests with API Coverage integration..."

TEST_PROFILE=${1:-api}
TIMESTAMP=$(date +"%Y%m%d_%H%M")

# Clean previous results
echo "🧹 Cleaning previous results..."
rm -rf target/allure-results/*
rm -rf target/swagger-coverage-output/*
rm -f swagger-coverage-*.html
rm -f swagger-coverage-*.json

# Run tests
echo "🧪 Running $TEST_PROFILE tests..."
./mvnw clean test -P$TEST_PROFILE

# Check if tests passed
if [ $? -eq 0 ]; then
    echo "✅ Tests completed successfully"
    
    # Generate API Coverage
    echo "📊 Generating API Coverage report..."
    ./integrate-api-coverage.sh
    
    # Generate Allure report
    echo "📋 Generating Allure report..."
    if command -v allure &> /dev/null; then
        allure generate target/allure-results --clean -o allure-report
        echo "📖 Allure report generated in: allure-report/"
        echo "🌐 Open report: allure open allure-report"
    else
        echo "⚠️  Allure CLI not found. Install it to generate reports:"
        echo "   npm install -g allure-commandline"
        echo "   Or use: java -jar allure-commandline.jar generate target/allure-results --clean -o allure-report"
    fi
    
    echo ""
    echo "🎉 Test execution completed successfully!"
    echo "📊 API Coverage: target/allure-results/swagger-coverage-report.html"
    echo "📋 Allure Report: allure-report/index.html"
    echo "📁 Test Results: target/allure-results/"
    
else
    echo "❌ Tests failed"
    exit 1
fi