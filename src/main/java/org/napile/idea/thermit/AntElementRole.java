/*
 * Copyright 2000-2009 JetBrains s.r.o.
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
package org.napile.idea.thermit;

import javax.swing.Icon;

import org.jetbrains.annotations.NonNls;
import com.intellij.icons.AllIcons;
import com.intellij.util.PlatformIcons;

/**
 * @author dyoma
 */
public enum AntElementRole implements PlatformIcons
{
	TARGET_ROLE(ThermitBundle.message("ant.role.ant.target"), AllIcons.Ant.Target),
	PROPERTY_ROLE(ThermitBundle.message("ant.role.ant.property"), PROPERTY_ICON),
	TASK_ROLE(ThermitBundle.message("ant.role.ant.task"), TASK_ICON),
	USER_TASK_ROLE(ThermitBundle.message("ant.element.role.user.task"), TASK_ICON),
	PROJECT_ROLE(ThermitBundle.message("ant.element.role.ant.project.name"), PROPERTY_ICON),
	MACRODEF_ROLE(ThermitBundle.message("ant.element.role.macrodef.element"), TASK_ICON),
	SCRIPTDEF_ROLE(ThermitBundle.message("ant.element.role.scriptdef.element"), TASK_ICON),
	@NonNls NULL_ROLE("Ant element", null);

	AntElementRole(String name, Icon icon)
	{
		myName = name;
		myIcon = icon;
	}

	private final String myName;
	private final Icon myIcon;

	public String getName()
	{
		return myName;
	}

	public Icon getIcon()
	{
		return myIcon;
	}
}
