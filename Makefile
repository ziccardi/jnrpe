PLATFORM=$(shell uname -s | tr A-Z a-z)
MKFILE_PATH := $(abspath $(lastword $(MAKEFILE_LIST)))
PROJECT_PATH := $(patsubst %/,%,$(dir $(MKFILE_PATH)))
BUILD_PATH="${PROJECT_PATH}/build"
BUILD_LOG_PATH="${PROJECT_PATH}/.logs"


REPORT_PATH="${BUILD_PATH}/report"
TEST_REPORT_PATH="${REPORT_PATH}/test"
COVERAGE_REPORT_PATH="${REPORT_PATH}/coverage"

.DEFAULT_GOAL := help

GIT_USERNAME ?= $(error Please set GIT_USERNAME to your github username)

clean: prepare/build clean/dist clean/website
	@(${PROJECT_PATH}/scripts/run_task.sh "Cleaning build files" "${PROJECT_PATH}/gradlew clean" "${BUILD_LOG_PATH}/$(subst /,-,$@).log")
.PHONY: clean

clean/dist:
	@(${PROJECT_PATH}/scripts/run_task.sh "Cleaning distribution" "rm -rf '${PROJECT_PATH}/dist'" "${BUILD_LOG_PATH}/$(subst /,-,$@).log")
.PHONY: clean/dist

clean/website:
	@(${PROJECT_PATH}/scripts/run_task.sh "Cleaning website" "rm -rf '${PROJECT_PATH}/website/build' '${PROJECT_PATH}/website/node_modules'" "${BUILD_LOG_PATH}/$(subst /,-,$@).log")
.PHONY: clean/website


dist: build
	@rm -rf ${PROJECT_PATH}/dist
	@mkdir ${PROJECT_PATH}/dist
	@cp ${PROJECT_PATH}/it.jnrpe.server/build/distributions/* ${PROJECT_PATH}/dist
.PHONY: dist

run: dist
	@tar xf ${PROJECT_PATH}/dist/jnrpe.tar -C ${PROJECT_PATH}/dist
	@${PROJECT_PATH}/dist/jnrpe/bin/jnrpe $(ARGS)
.PHONY: run

build: prepare/build
	@(${PROJECT_PATH}/scripts/run_task.sh "Building" "${PROJECT_PATH}/gradlew build -x test -x spotlessCheck -x spotbugsTest -x spotbugsMain -x javadoc" "${BUILD_LOG_PATH}/$(subst /,-,$@).log")
.PHONY: build

test: build
	@(${PROJECT_PATH}/scripts/run_task.sh "Testing" "${PROJECT_PATH}/gradlew build test -x spotlessCheck -x spotbugsTest -x spotbugsMain -x javadoc" "${BUILD_LOG_PATH}/$(subst /,-,$@).log")
.PHONY: test

test/report: test
	@rm -rf ${TEST_REPORT_PATH}
	@mkdir -p ${TEST_REPORT_PATH}
	@echo "<H1>JNRPE Test Report</H1>" > ${TEST_REPORT_PATH}/index.html
	@echo "<p>Here is the list of the test reports for each JNRPE module</p>" >> ${TEST_REPORT_PATH}/index.html
	@echo "<UL>" >> ${TEST_REPORT_PATH}/index.html
	@find ~+ -path \*/test/index.html -exec echo "<LI> <A HREF=\""{}"\">"{}"</A>" >> ${TEST_REPORT_PATH}/index.html \;
	@echo "</UL>" >> ${TEST_REPORT_PATH}/index.html
	@echo "Test report:     ${TEST_REPORT_PATH}/index.html"
.PHONY: test/report

test/coverage: test
	@rm -rf ${COVERAGE_REPORT_PATH}
	@mkdir -p ${COVERAGE_REPORT_PATH}
	@echo "<H1>JNRPE Test Coverage Report</H1>" > ${COVERAGE_REPORT_PATH}/index.html
	@echo "<p>Here is the list of the test coverage reports for each JNRPE module</p>" >> ${COVERAGE_REPORT_PATH}/index.html
	@echo "<UL>" >> ${COVERAGE_REPORT_PATH}/index.html
	@find ~+ -path \*/coverage/index.html -exec echo "<LI> <A HREF=\""{}"\">"{}"</A>" >> ${COVERAGE_REPORT_PATH}/index.html \;
	@echo "</UL>" >> ${COVERAGE_REPORT_PATH}/index.html
	@echo "Coverage report: ${COVERAGE_REPORT_PATH}/index.html"
.PHONY: test/coverage

test/all: test test/report test/coverage
.PHONY: test/all

style/check: prepare/build
	@(${PROJECT_PATH}/scripts/run_task.sh "Style checks" "${PROJECT_PATH}/gradlew spotlessCheck -x test -x spotbugsTest -x spotbugsMain -x javadoc" "${BUILD_LOG_PATH}/$(subst /,-,$@).log")
.PHONY: style/check

style/fix:
	@${PROJECT_PATH}/gradlew spotlessApply -x test -x spotbugsTest -x spotbugsMain -x javadoc
.PHONY: style/fix

code/check: prepare/build build
	@(${PROJECT_PATH}/scripts/run_task.sh "Formal checks" "${PROJECT_PATH}/gradlew spotbugsMain -x test -x spotlessCheck -x spotbugsTest -x spotbugsMain -x javadoc" "${BUILD_LOG_PATH}/$(subst /,-,$@).log")
.PHONY: code/check

website/prepare: prepare/build
	@(${PROJECT_PATH}/scripts/run_task.sh "Website prepare" "cd ${PROJECT_PATH}/website && npm ci" "${BUILD_LOG_PATH}/$(subst /,-,$@).log")
.PHONY: website/prepare

website/build: prepare/build website/prepare
	@(${PROJECT_PATH}/scripts/run_task.sh "Website build" "cd ${PROJECT_PATH}/website && npm run build" "${BUILD_LOG_PATH}/$(subst /,-,$@).log")
.PHONY: website/build

website/run: website/prepare
	@cd ${PROJECT_PATH}/website \
		&& npm run start
.PHONY: website/run

website/publish:prepare/build website/build
	@(${PROJECT_PATH}/scripts/run_task.sh "Website publish" "cd ${PROJECT_PATH}/website && GIT_USER=$(GIT_USERNAME) CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD) USE_SSH=true npm run publish-gh-pages" "${BUILD_LOG_PATH}/$(subst /,-,$@).log")
.PHONY: website/publish

all: clean style/check build code/check test website/build dist
.PHONY: all

prepare/build:
	@mkdir -p "${BUILD_PATH}" "${BUILD_LOG_PATH}"
help:
	@echo "JNRPE make targets"
	@echo ""
	@echo "make clean                                             cleans the build folder"
	@echo "make build                                             builds the code"
	@echo "make dist                                              create a distribution into the dist folder"
	@echo "make test                                              run the unit tests"
	@echo "make run                                               run JNRPE. Custom arguments can be specified through the ARGS env variable"
	@echo "make test/report                                       generates the test execution report"
	@echo "make test/coverage                                     generates the test coverage report"
	@echo "make test/all                                          run the tests and generates the reports"
	@echo "make style/check                                       lints the java sources"
	@echo "make style/fix                                         applies code style to the java sources"
	@echo "make code/check                                        runs static analysis"
	@echo "make website/prepare                                   installs all the website dependencies"
	@echo "make website/build                                     compile the website for deployment"
	@echo "make website/publish                                   publish the website on github pages"
.PHONY: help
