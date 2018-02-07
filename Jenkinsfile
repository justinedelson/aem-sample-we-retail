#!groovy
@Library(['com.adobe.qe.evergreen.sprout@prerelease'])
import com.adobe.qe.evergreen.sprout.*
import com.adobe.qe.evergreen.sprout.config.*
import com.adobe.qe.evergreen.sprout.criteria.*
import com.adobe.qe.evergreen.sprout.model.*
import com.adobe.qe.evergreen.sprout.command.*
import com.adobe.qe.evergreen.sprout.vcs.RepositoryConfig

String MINION_HUB_URL = 'http://qa-bsl-minion-hub.corp.adobe.com:8811'

TEST_GROUP_1 =  "aem.samplecontent.we-retail.tests.formhidden," +
        "aem.samplecontent.we-retail.tests.formoptions," +
        "aem.samplecontent.we-retail.tests.formcomponents," +
        "aem.samplecontent.we-retail.tests.breadcrumb," +
        "aem.samplecontent.we-retail.tests.teaser"

TEST_GROUP_2 =  "aem.samplecontent.we-retail.tests.formtext," +
        "aem.samplecontent.we-retail.tests.languagenavigation," +
        "aem.samplecontent.we-retail.tests.text," +
        "aem.samplecontent.we-retail.tests.title," +
        "aem.samplecontent.we-retail.tests.formbutton"

TEST_GROUP_3 =  "aem.samplecontent.we-retail.tests.image," +
        "aem.samplecontent.we-retail.tests.list," +
        "aem.samplecontent.we-retail.tests.formcontainer"

TEST_GROUP_4 =  "aem.samplecontent.we-retail.tests.navigation," +
        "aem.samplecontent.we-retail.tests.page," +
        "aem.samplecontent.we-retail.tests.search"

TEST_GROUP_5 =  "aem.samplecontent.we-retail.tests.check-content"

// Define Jenkins Build Paramaters so we can select what browser to use for UI testing
BuildVariable RUN_ON_CHROME = new BuildVariable()
        .withName('RUN_ON_CHROME')
        .withJenkinsClass("BooleanParameterDefinition")
        .withDefaultValue(true)
        .withDescription('Runs UI tests against Chrome, enabled by default')
        .build()

BuildVariable RUN_ON_FIREFOX = new BuildVariable()
        .withName('RUN_ON_FIREFOX')
        .withJenkinsClass("BooleanParameterDefinition")
        .withDefaultValue(true)
        .withDescription('Runs UI tests against Firefox, enabled by default')
        .build()

BuildVariable RUN_ON_EDGE = new BuildVariable()
        .withName('RUN_ON_EDGE')
        .withJenkinsClass("BooleanParameterDefinition")
        .withDefaultValue(false)
        .withDescription('Runs UI tests against Edge, disabled by default')
        .build()

// add them to the list of build parameters
BuildVariables.BUILD_VARIABLES.add(RUN_ON_CHROME)
BuildVariables.BUILD_VARIABLES.add(RUN_ON_EDGE)
BuildVariables.BUILD_VARIABLES.add(RUN_ON_FIREFOX)

/* --------------------------------------------------------------------- */
/*                                MODULES                                */
/* --------------------------------------------------------------------- */

// we retail modules
Module weRetailCore = new Module.Builder('main/core')
        .withUnitTests(true)
        .withCoverage(true)
        .withRelease()
        .withArtifact('jar', 'main/core/target/we.retail.core-*.jar', true)
        .build()
Module weRetailUIContent = new Module.Builder('main/ui.content')
        .withRelease()
        .withArtifact('zip', 'main/ui.content/target/we.retail.ui.content-*.zip', true)
        .build()
Module weRetailUIApps = new Module.Builder('main/ui.apps')
        .withRelease()
        .withArtifact('zip', 'main/ui.apps/target/we.retail.ui.apps-*.zip', true)
        .build()
Module weRetailConfig = new Module.Builder('main/config')
        .withRelease()
        .withArtifact('zip', 'main/config/target/we.retail.config-*.zip', true)
        .build()
Module weRetailItUi = new Module.Builder('main/it.tests.ui-js')
        .withArtifact('zip', 'main/it.tests.ui-js/target/we.retail.it.tests.ui-js-*.zip', true)
        .build()
Module weRetailAll = new Module.Builder('main/all')
        .withArtifact('zip', 'main/all/target/we.retail.all-*.zip', true)
        .build()

// commerce test modules
Module commerceItHttp = new Module.Builder('commerce/it/http')
        .withMavenArtifact("jar", 'commerce/it/http/target/com.adobe.cq.commerce.it.http-*-integrationtest.jar')
        .build()

/* --------------------------------------------------------------------- */
/*                        EXTERNAL DEPENDENCIES                          */
/* --------------------------------------------------------------------- */
// Hobbes tests dependencies
MavenDependency hobbesRewriterPackage = new MavenDependency.Builder()
        .withGroupId("com.adobe.granite")
        .withArtifactId("com.adobe.granite.testing.hobbes.rewriter")
        .withVersion("latest")
        .withExtension("jar")
        .build()

MavenDependency uiTestingCommonsPackage = new MavenDependency.Builder()
        .withGroupId("com.adobe.qe")
        .withArtifactId("com.adobe.qe.ui-testing-commons")
        .withVersion("latest")
        .withExtension("zip")
        .build()

MavenDependency coreComponentsPackage = new MavenDependency.Builder()
        .withGroupId("com.adobe.cq")
        .withArtifactId("core.wcm.components.all")
        .withVersion("2.0.0")
        .withExtension("zip")
        .build()

// we retail product sample content
MavenDependency weRetailSampleContentPackage = new MavenDependency.Builder()
        .withGroupId("com.adobe.aem.sample")
        .withArtifactId("we.retail.commons.content")
        .withVersion("latest")
        .withExtension("zip")
        .build()

MavenDependency itJunitCore = new MavenDependency.Builder()
        .withGroupId("org.apache.sling")
        .withArtifactId("org.apache.sling.junit.core")
        .withVersion("1.0.23")
        .withExtension("jar")
        .build()

/* --------------------------------------------------------------------- */
/*                       QUICKSTART CONFIGURATION                        */
/* --------------------------------------------------------------------- */
Quickstart quickstart = new BuildQuickstart.Builder('Quickstart 6.4')
        .withModule(weRetailCore)
        .withModule(weRetailUIContent)
        .withModule(weRetailUIApps)
        .withModule(weRetailConfig)
        .withModule(commerceItHttp)
        .build()

/* --------------------------------------------------------------------- */
/*                      CQ INSTANCE CONFIGURATION                        */
/* --------------------------------------------------------------------- */
CQInstance author = new CQInstance.Builder()
        .withQuickstart(quickstart)
        .withId('weretail-author')
        .withPort(1234)
        .withRunmode("author")
        .withContextPath("/cp")
        .withMavenDependency(hobbesRewriterPackage)
        .withMavenDependency(uiTestingCommonsPackage)
        .withMavenDependency(coreComponentsPackage)
        .withMavenDependency(weRetailSampleContentPackage)
        .withFileDependency(weRetailItUi.getArtifact('zip'))
        .build()

CQInstance publish = new CQInstance.Builder()
        .withQuickstart(quickstart)
        .withId('weretail-publish')
        .withPort(4503)
        .withRunmode("publish")
        .withContextPath("/cp")
        .withMavenDependency(itJunitCore).build()

/* --------------------------------------------------------------------- */
/*                          INTEGRATION TESTS                            */
/* --------------------------------------------------------------------- */
IntegrationTestRun weretailIt = new IntegrationTestRun.Builder()
        .withName('IT Commerce We.Retail')
        .withBundle('commerce/it/http')
        .withInstance(author)
        .withInstance(publish)
        .withAdditionalParam('-Pcategory-weretail')
        .build()

/* --------------------------------------------------------------------- */
/*                                UI TESTS                               */
/* --------------------------------------------------------------------- */

// define criterias based on Jenkins params to decide what browsers to use for UI testing
Criteria FIREFOX_CRITERIA = { SproutEffectiveConfig conf, Object jenkins -> return jenkins.env.RUN_ON_FIREFOX == null ? false : jenkins.env.RUN_ON_FIREFOX.toBoolean()}
Criteria CHROME_CRITERIA = { SproutEffectiveConfig conf, Object jenkins -> return jenkins.env.RUN_ON_CHROME == null ? true : jenkins.env.RUN_ON_CHROME.toBoolean()}
Criteria EDGE_CRITERIA = { SproutEffectiveConfig conf, Object jenkins -> return jenkins.env.RUN_ON_EDGE == null ? false: jenkins.env.RUN_ON_EDGE.toBoolean()}

UITestRun coreCompUIChromePart1 = new UITestRun.Builder()
        .withName('Test Group 1 / Chrome')
        .withInstance(author)
        .withBrowser('CHROME')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withFilter(TEST_GROUP_1)
        .withCriteria(CHROME_CRITERIA)
        .build()

UITestRun coreCompUIChromePart2 = new UITestRun.Builder()
        .withName('Test Group 2 / Chrome')
        .withInstance(author)
        .withBrowser('CHROME')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withCriteria(CHROME_CRITERIA)
        .withFilter(TEST_GROUP_2)
        .build()

UITestRun coreCompUIChromePart3 = new UITestRun.Builder()
        .withName('Test Group 3 / Chrome')
        .withInstance(author)
        .withBrowser('CHROME')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withCriteria(CHROME_CRITERIA)
        .withFilter(TEST_GROUP_3)
        .build()

UITestRun coreCompUIChromePart4 = new UITestRun.Builder()
        .withName('Test Group 4 / Chrome')
        .withInstance(author)
        .withBrowser('CHROME')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withCriteria(CHROME_CRITERIA)
        .withFilter(TEST_GROUP_4)
        .build()

UITestRun coreCompUIChromePart5 = new UITestRun.Builder()
        .withName('Test Group 5 / Chrome')
        .withInstance(author)
        .withBrowser('CHROME')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withCriteria(CHROME_CRITERIA)
        .withFilter(TEST_GROUP_5)
        .build()

// Run against firefox
UITestRun coreCompUIFirefoxPart1 = new UITestRun.Builder()
        .withName('Test Group 1 / Firefox')
        .withInstance(author)
        .withBrowser('FIREFOX')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withFilter(TEST_GROUP_1)
        .withCriteria(FIREFOX_CRITERIA)
        .build()

UITestRun coreCompUIFirefoxPart2 = new UITestRun.Builder()
        .withName('Test Group 2 / Firefox')
        .withInstance(author)
        .withBrowser('FIREFOX')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withCriteria(FIREFOX_CRITERIA)
        .withFilter(TEST_GROUP_2)
        .build()

UITestRun coreCompUIFirefoxPart3 = new UITestRun.Builder()
        .withName('Test Group 3 / Firefox')
        .withInstance(author)
        .withBrowser('FIREFOX')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withCriteria(FIREFOX_CRITERIA)
        .withFilter(TEST_GROUP_3)
        .build()

UITestRun coreCompUIFirefoxPart4 = new UITestRun.Builder()
        .withName('Test Group 4 / Firefox')
        .withInstance(author)
        .withBrowser('FIREFOX')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withCriteria(FIREFOX_CRITERIA)
        .withFilter(TEST_GROUP_4)
        .build()

UITestRun coreCompUIFirefoxPart5 = new UITestRun.Builder()
        .withName('Test Group 5 / Firefox')
        .withInstance(author)
        .withBrowser('FIREFOX')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withCriteria(FIREFOX_CRITERIA)
        .withFilter(TEST_GROUP_5)
        .build()

// Run against edge
UITestRun coreCompUIEdgePart1 = new UITestRun.Builder()
        .withName('Test Group 1 / Edge')
        .withInstance(author)
        .withBrowser('EDGE')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withFilter(TEST_GROUP_1)
        .withCriteria(EDGE_CRITERIA)
        .build()

UITestRun coreCompUIEdgePart2 = new UITestRun.Builder()
        .withName('Test Group 2 / Edge')
        .withInstance(author)
        .withBrowser('EDGE')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withCriteria(EDGE_CRITERIA)
        .withFilter(TEST_GROUP_2)
        .build()

UITestRun coreCompUIEdgePart3 = new UITestRun.Builder()
        .withName('Test Group 3 / Edge')
        .withInstance(author)
        .withBrowser('EDGE')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withCriteria(EDGE_CRITERIA)
        .withFilter(TEST_GROUP_3)
        .build()

UITestRun coreCompUIEdgePart4 = new UITestRun.Builder()
        .withName('Test Group 4 / Edge')
        .withInstance(author)
        .withBrowser('EDGE')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withCriteria(EDGE_CRITERIA)
        .withFilter(TEST_GROUP_4)
        .build()

UITestRun coreCompUIEdgePart5 = new UITestRun.Builder()
        .withName('Test Group 5 / Edge')
        .withInstance(author)
        .withBrowser('EDGE')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withCriteria(EDGE_CRITERIA)
        .withFilter(TEST_GROUP_5)
        .build()

/* --------------------------------------------------------------------- */
/*                       SPROUT CONFIGURATION                            */
/* --------------------------------------------------------------------- */
SproutConfig config = new SproutConfig()

// additional repo for getting the latest core component sources
RepositoryConfig coreComponentsRepo = new RepositoryConfig('git@git.corp.adobe.com:CQ/aem-core-wcm-components.git')
        .withDefaultBranch('PRIVATE_master')
        .withFolder('core-comp')
        .withVCS('git')
        .build()

RepositoryConfig commerceRepo = new RepositoryConfig('git@git.corp.adobe.com:CQ/commerce.git')
	.withDefaultBranch('master')
        .withFolder('commerce')
        .withVCS('git')
        .build()	

config.setRepositories([coreComponentsRepo, commerceRepo])

// calculate code
config.setComputeCoverage(true)
config.setComputeReleaseCoverage(true)

// only for the PRIVATE_master branch
config.setCoverageCriteria([new Branch(/^PRIVATE_master$/)])
config.setReleaseCoverageCriteria([new Branch(/^PRIVATE_master$/)])

config.setSonarSnapshotPrefix('WE-RETAIL-SAMPLE-SPROUT-PRIVATE_MASTER-')
config.setSonarReleasePrefix('WE-RETAIL-SAMPLE-SPROUT-PRIVATE_MASTER-')

// Report Sprout stats to elasticsearch
config.getElasticsearchReporting().setEnable(true)

// the modules to build
config.setModules([weRetailAll, weRetailCore, weRetailUIContent, weRetailUIApps, weRetailConfig, weRetailItUi
                   , commerceItHttp])

// the tests to execute
config.setTestRuns([coreCompUIChromePart1,coreCompUIChromePart2,coreCompUIChromePart3,coreCompUIChromePart4,
                    coreCompUIChromePart5,
                    coreCompUIFirefoxPart1,coreCompUIFirefoxPart2,coreCompUIFirefoxPart3,coreCompUIFirefoxPart4,
                    coreCompUIFirefoxPart5,
                    coreCompUIEdgePart1,coreCompUIEdgePart2,coreCompUIEdgePart3,coreCompUIEdgePart4,
                    coreCompUIEdgePart5,
                    weretailIt])

// Releases
config.setReleaseCriteria([new Branch(/^PRIVATE_master$/)])
config.setQuickstartPRCriteria([new Branch(/^PRIVATE_master$/)])

// don't ask for release at the end
config.setEnableBuildPromotion(false)

// Enable use of Jenkins parameters on this branch when manual triggering it to set
// release info and select which browser to use
config.setParameterDefinitionCriteria([ new Branch(/^PRIVATE_master$/)])

config.setGithubAccessTokenId('bf3be1a6-ad0a-43d9-86e2-93b30279060f')
config.setQuickstartPRConfig(quickstart)

config.setEnableMailNotification(false)

// Don't trigger sprout for release commits or any @releng commits
config.setBuildCriteria([new Exclude(
        new AndCriteria()
                .withCriteria(new GitCommitMessage(/^(.*)(@releng|NPR-84|@docs)(.*)$/))
                .withCriteria(new Exclude(new ManuallyTriggered())))])

// Slack notification
config.setEnableSlackNotifications(true)
config.setSlackChannel('#refsquad-sprouts')
config.setSlackTeamDomain('cq-dev')
config.setSlackIntegrationToken('TPhlDoZqT0DvKyVC1RnCvzfj')

/* --------------------------------------------------------------------- */
/*                       SPROUT CUSTOMIZATION                            */
/* --------------------------------------------------------------------- */
Pipeline sprout = new Sprout.Builder()
        .withConfig(config)
        .withJenkins(this).build()

sprout.execute()
