/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.napile.idea.thermit.config.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.napile.idea.thermit.ThermitBundle;
import org.napile.idea.thermit.config.ThermitConfigurationBase;
import com.intellij.ide.macro.MacroManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.config.AbstractProperty;
import com.intellij.util.config.ExternalizablePropertyContainer;
import com.intellij.util.config.ListProperty;
import com.intellij.util.config.StorageProperty;
import com.intellij.util.config.ValueProperty;
import com.intellij.util.containers.ContainerUtil;

public class GlobalThermitConfiguration implements ApplicationComponent, JDOMExternalizable
{
	private static final Logger LOG = Logger.getInstance("#org.napile.idea.thermit.config.impl.AntGlobalConfiguration");
	public static final StorageProperty FILTERS_TABLE_LAYOUT = new StorageProperty("filtersTableLayout");
	public static final StorageProperty PROPERTIES_TABLE_LAYOUT = new StorageProperty("propertiesTableLayout");
	static final ListProperty<AntInstallation> ANTS = ListProperty.create("registeredThermits");
	private final ExternalizablePropertyContainer myProperties = new ExternalizablePropertyContainer();
	private final AntInstallation myBundledAnt;
	public static final String BUNDLED_ANT_NAME = ThermitBundle.message("ant.reference.bundled.ant.name");
	public final Condition<AntInstallation> IS_USER_ANT = new Condition<AntInstallation>()
	{
		public boolean value(AntInstallation antInstallation)
		{
			return antInstallation != myBundledAnt;
		}
	};

	public static final AbstractProperty<GlobalThermitConfiguration> INSTANCE = new ValueProperty<GlobalThermitConfiguration>("$GlobalThermitConfiguration.INSTANCE", null);
	@NonNls
	public static final String ANT_FILE = "thermit";

	@NonNls
	public static final String ANT_JAR_FILE_NAME = "/lib/thermit.nzip";
	@NonNls
	public static final String PLUGIN_LIB_DIR = "/idea.thermit/lib/thermit";

	public GlobalThermitConfiguration()
	{
		myProperties.registerProperty(FILTERS_TABLE_LAYOUT);
		myProperties.registerProperty(PROPERTIES_TABLE_LAYOUT);
		myProperties.registerProperty(ANTS, ANT_FILE, AntInstallation.EXTERNALIZER);
		INSTANCE.set(myProperties, this);
		myProperties.rememberKey(INSTANCE);

		myBundledAnt = createBundledAnt();
	}

	@NotNull
	public String getComponentName()
	{
		return "GlobalThermitConfiguration";
	}

	public void initComponent()
	{
	}

	public static AntInstallation createBundledAnt()
	{
		AntInstallation bundledAnt = new AntInstallation()
		{
			public AntReference getReference()
			{
				return AntReference.BUNDLED_ANT;
			}
		};

		File antHome = findCorrectLibHome();
		AntInstallation.NAME.set(bundledAnt.getProperties(), BUNDLED_ANT_NAME);
		AntInstallation.HOME_DIR.set(bundledAnt.getProperties(), antHome.getAbsolutePath());

		ArrayList<AntClasspathEntry> classpath = AntInstallation.CLASS_PATH.getModifiableList(bundledAnt.getProperties());

		classpath.add(new AllNZipsUnderDirEntry(new File(antHome, "lib")));
		bundledAnt.updateVersion(new File(antHome, ANT_JAR_FILE_NAME));
		return bundledAnt;
	}

	private static File findCorrectLibHome()
	{
		// work if when u develop plugin
		File antHome = new File(PathManager.getPluginsPath() + PLUGIN_LIB_DIR);
		if(antHome.exists() && new File(antHome, ANT_JAR_FILE_NAME).exists())
			return antHome;

		// search in bundled plugins
		antHome = new File(PathManager.getHomePath());
		if(antHome.exists())
		{
			antHome = new File(antHome, "plugins" + PLUGIN_LIB_DIR);
			if(antHome.exists() && new File(antHome, ANT_JAR_FILE_NAME).exists())
				return antHome;
		}
		throw new UnsupportedOperationException("Cant find thermit bundled lib");
	}

	public void disposeComponent()
	{
	}

	public void readExternal(Element element) throws InvalidDataException
	{
		myProperties.readExternal(element);
	}

	public void writeExternal(Element element) throws WriteExternalException
	{
		myProperties.writeExternal(element);
	}

	public static GlobalThermitConfiguration getInstance()
	{
		return ApplicationManager.getApplication().getComponent(GlobalThermitConfiguration.class);
	}

	public Map<AntReference, AntInstallation> getConfiguredAnts()
	{
		Map<AntReference, AntInstallation> map = ContainerUtil.newMapFromValues(ANTS.getIterator(getProperties()), AntInstallation.REFERENCE_TO_ANT);
		map.put(AntReference.BUNDLED_ANT, myBundledAnt);
		return map;
	}

	public AntInstallation getBundledAnt()
	{
		return myBundledAnt;
	}

	public AbstractProperty.AbstractPropertyContainer getProperties()
	{
		return myProperties;
	}

	public AbstractProperty.AbstractPropertyContainer getProperties(Project project)
	{
		return new CompositePropertyContainer(new AbstractProperty.AbstractPropertyContainer[]{
				myProperties,
				ThermitConfigurationBase.getInstance(project).getProperties()
		});
	}

	public void addConfiguration(final AntInstallation ant)
	{
		if(getConfiguredAnts().containsKey(ant.getReference()))
		{
			LOG.error("Duplicate name: " + ant.getName());
		}
		ANTS.getModifiableList(getProperties()).add(ant);
	}

	public void removeConfiguration(final AntInstallation ant)
	{
		ANTS.getModifiableList(getProperties()).remove(ant);
	}

	public static Sdk findJdk(final String jdkName)
	{
		return ProjectJdkTable.getInstance().findJdk(jdkName);
	}

	public static MacroManager getMacroManager()
	{
		return MacroManager.getInstance();
	}
}
