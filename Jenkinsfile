#!groovy
@Library(['com.adobe.qe.evergreen.sprout@issue/fixing-testpath'])
import com.adobe.qe.evergreen.sprout.*
import com.adobe.qe.evergreen.sprout.criteria.*
import com.adobe.qe.evergreen.sprout.model.*

String MINION_HUB_URL = 'http://qa-bsl-minion-hub.corp.adobe.com:8811'

TEST_GROUP_1 =  "aem.samplecontent.we-retail.tests.formhidden," +
        "aem.samplecontent.we-retail.tests.formoptions," +
        "aem.samplecontent.we-retail.tests.formcomponents," +
        "aem.samplecontent.we-retail.tests.breadcrumb," +
        "aem.samplecontent.we-retail.tests.teaser"

TEST_GROUP_2 =  "aem.samplecontent.we-retail.tests.formtext," +
        "aem.samplecontent.we-retail.tests.languagenavigation," +
        "aem.samplecontent.we-retail.tests.text," +
        "aem.samplecontent.we-retail.tests.formbutton"

TEST_GROUP_3 =  "aem.samplecontent.we-retail.tests.image," +
        "aem.samplecontent.we-retail.tests.list," +
        "aem.samplecontent.we-retail.tests.formcontainer"

TEST_GROUP_4 =  "aem.samplecontent.we-retail.tests.navigation," +
        "aem.samplecontent.we-retail.tests.page," +
        "aem.samplecontent.we-retail.tests.search"

TEST_GROUP_5 =  "aem.samplecontent.we-retail.tests.check-content"


/* --------------------------------------------------------------------- */
/*                                MODULES                                */
/* --------------------------------------------------------------------- */

// we retail modules
Module weRetailCore = new Module.Builder('main/core')
        .withUnitTests(true)
        .withCoverage()
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

// core component modules
Module componentsCore = new Module.Builder('core-comp/bundles/core')
        .withArtifact('jar', 'core-comp/bundles/core/target/core.wcm.components.sandbox.bundle-*.jar', true)
        .build()
Module componentsJUnit = new Module.Builder('core-comp/testing/junit/core')
        .withArtifact('jar', 'core-comp/testing/junit/core/target/core.wcm.components.junit.core-*.jar', true)
        .withConsumer(componentsCore)
        .build()
Module componentsContent = new Module.Builder('core-comp/content')
        .withArtifact('zip', 'core-comp/content/target/core.wcm.components.sandbox.content-*.zip', true)
        .build()
Module componentsConfig = new Module.Builder('core-comp/config')
        .withArtifact('zip', 'core-comp/config/target/core.wcm.components.sandbox.config-*.zip', true)
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
        .withModule(componentsCore)
        .withModule(componentsContent)
        .withModule(componentsConfig)
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
        .withAdditionalParam('-Pcategory-weretail').build()

/* --------------------------------------------------------------------- */
/*                                UI TESTS                               */
/* --------------------------------------------------------------------- */
UITestRun coreCompUIChromePart1 = new UITestRun.Builder()
        .withName('Test Group 1 / Chrome')
        .withInstance(author)
        .withBrowser('CHROME')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/jenkinsfiles/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withFilter(TEST_GROUP_1)
        .build()

UITestRun coreCompUIChromePart2 = new UITestRun.Builder()
        .withName('Test Group 2 / Chrome')
        .withInstance(author)
        .withBrowser('CHROME')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/jenkinsfiles/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withFilter(TEST_GROUP_2)
        .build()

UITestRun coreCompUIChromePart3 = new UITestRun.Builder()
        .withName('Test Group 3 / Chrome')
        .withInstance(author)
        .withBrowser('CHROME')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/jenkinsfiles/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withFilter(TEST_GROUP_3)
        .build()

UITestRun coreCompUIChromePart4 = new UITestRun.Builder()
        .withName('Test Group 4 / Chrome')
        .withInstance(author)
        .withBrowser('CHROME')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/jenkinsfiles/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withFilter(TEST_GROUP_4)
        .build()

UITestRun coreCompUIChromePart5 = new UITestRun.Builder()
        .withName('Test Group 5 / Chrome')
        .withInstance(author)
        .withBrowser('CHROME')
        .withHobbesHubUrl(MINION_HUB_URL)
        .withRunInstructions('main/jenkinsfiles/UITestRunOptions.json')
        .withWaitForMinionMinutes(10)
        .withFilter(TEST_GROUP_5)
        .build()

/* --------------------------------------------------------------------- */
/*                       SPROUT CONFIGURATION                            */
/* --------------------------------------------------------------------- */
SproutConfig config = new SproutConfig()

// additional repo for getting the latest core component sources
config.setAdditionalRepositories([
        [url: 'git@git.corp.adobe.com:CQ/aem-core-wcm-components.git', branch: 'PRIVATE_master', folder: 'core-comp', vcs: 'git'],
        [url: 'git@git.corp.adobe.com:CQ/commerce.git', branch: 'master', folder: 'commerce', vcs: 'git']
])

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
config.setModules([componentsJUnit,componentsCore, componentsContent, componentsConfig,
                   weRetailAll,weRetailCore, weRetailUIContent, weRetailUIApps, weRetailConfig, weRetailItUi
                   , commerceItHttp])

// the tests to execute
config.setTestRuns([coreCompUIChromePart1,coreCompUIChromePart2,coreCompUIChromePart3,coreCompUIChromePart4,
                    coreCompUIChromePart5, weretailIt])

// Releases
config.setReleaseCriteria([new Branch(/^PRIVATE_master$/)])
config.setQuickstartPRCriteria([new Branch(/^PRIVATE_master$/)])

// don't ask for release at the end
config.setEnableBuildPromotion(false)
// use parameterized build on this branch when manual triggering to set release info
config.setParameterDefinitionCriteria([ new Branch(/^PRIVATE_master$/)])

config.setGithubAccessTokenId('bf3be1a6-ad0a-43d9-86e2-93b30279060f')
config.setQuickstartPRConfig(quickstart)

config.setEnableMailNotification(false)

// Don't trigger sprout for release commits
config.setBuildCriteria([new Exclude(
        new AndCriteria()
                .withCriteria(new GitCommitMessage(/^(.*)@releng \[maven\-scm\] :prepare(.*)$/))
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
