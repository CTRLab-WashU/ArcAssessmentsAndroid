# Arc Assessments Android Library

The Dominantly Inherited Alzheimer Network (DIAN) is a research group that focuses on studying early-onset, genetically inherited forms of Alzheimer’s disease and is based in Washington University in St. Louis, MO, USA. This library contains the assessments for Ambulatory research in cognition (ARC), a DIAN study that involves participants completing brief, game-like tests on their smartphones in order to examine levels of cognitive function over time. 

## Gradle Import

To import this library into an existing project, you must add the Github Package repository to your project level build.gradle file.  

`
	repositories {
	    maven {
	        url = uri("https://maven.pkg.github.com/CTRLab-WashU/ArcAssessmentsAndroid")
	        credentials {
	            /** Add to gradle.properties in root project folder file with
	             ** gpr.usr=GITHUB_USER_ID & gpr.key=PERSONAL_ACCESS_TOKEN
	             ** Set env variable GPR_USER & GPR_API_KEY if not adding a properties file**/
	            username = project.findProperty('gpr.usr') ?: System.getenv("GPR_USER")
	            password = project.findProperty('gpr.key') ?: System.getenv("GPR_TOKEN")
	        }
	    }
	}
`

This requires you to put your git username and github personal access token with permission "read packages" either in local.proprties or as an environment variable on your local machine. Not that the syntax of this may vary based on your version of gradle.

Then, you can simple add these two lines to your app's build.gradle.

`
	// WashU Arc Assessments
    implementation("com.github.gcacace:signature-pad:1.3.1")
    implementation("edu.wustl.arc.assessments:core-library:0.0.1")
`

### Sample App

The Sample App shows how you can launch the assessments and read the results.  There are two classes that you need to include to integrate the ARC library into your existing project. 

### SampleAppStateMachine.java

This class controls which screens show after luanching the assessments and in which order.  You can change the way this operates to your desired outcome, but include **SampleAppStateMachine** in your project to start.  The SampleAppStateMachine is injected into the Arc.java class, in the **SampleApplication**

### SampleApplication.java

This class is your standard Android Application class. In the **onCreate()** function you need to initialize the Arc library and provide your custom StateMachine class to it.  At this point, you can also customize the assessment library based on various config variables.

### Assessment localization

The library ships with support for many different languages.  The language the library uses is not the default Android device language, but needs set using:

`
	PreferencesManager.getInstance().putStringImmediately(Locale.TAG_LANGUAGE, language);
	PreferencesManager.getInstance().putStringImmediately(Locale.TAG_COUNTRY, country);
`

Then, you must make sure that Android Context is updated to reflect these changes:

`
	Locale locale = new Locale(languageCode);
	Locale.setDefault(locale);
	Resources resources = activity.getResources();
	Configuration config = resources.getConfiguration();
	config.setLocale(locale);
	resources.updateConfiguration(config, resources.getDisplayMetrics());
`

Some devices require the app to be restarted for these to take effect.

### Application Structure

**edu.wustl.arc.core.Config**

The `Config` class is a static class that defines many different configuration options. These values are modified based on the needs of the specific application and the current Build Variant.

**Application**

The core `Application` class handles initializing several components, as well as some lifecycle events. 

Each application provides an `Application` subclass, which override the `onCreate()` and `registerStudyComponents()` methods. The `onCreate()` method typically handles customizing the `Config` values for the application.

**StateMachine**

Each application contains a subclass of the `edu.wustl.arc.study.StateMachineAlpha` class (which itself is a subclass of `edu.wustl.arc.study.StateMachine`). This class manages pretty much the entire application state.

There are two key properties on the `StateMachine` object:
- *state*, a `State` object which contains the `lifecycle` and `currentPath` values
- *statecache*, a `StateCache` object which contains a list of upcoming Fragments.

The `decidePath()` method uses the state's current `lifecycle` and `currentPath` values to decide the next value of `currentPath`.
The `setupPath()` method is usually called right after `decidePath()`. Based on the current state, it adds the necessary fragments to the `cache`.

The `StateMachine` contains several other methods that handle moving through items within the `cache`, such as `openNext()`, `skipToNextSegment()`, `moveOn()`, etc.
