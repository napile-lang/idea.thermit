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
package org.napile.idea.thermit.config.impl;

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.util.StringBuilderSpinAllocator;
import com.intellij.util.containers.Convertor;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;

@Tag("build-property")
public final class BuildFileProperty implements JDOMExternalizable
{
	@NonNls
	private static final String NAME = "name";
	@NonNls
	private static final String VALUE = "value";
	private String myPropertyName;
	private String myPropertyValue;
	public static final Convertor<BuildFileProperty, String> TO_COMMAND_LINE = new Convertor<BuildFileProperty, String>()
	{
		@NonNls
		public String convert(BuildFileProperty buildFileProperty)
		{
			@NonNls final StringBuilder builder = StringBuilderSpinAllocator.alloc();
			try
			{
				builder.append("-D");
				builder.append(buildFileProperty.getPropertyName());
				builder.append('=');
				builder.append(buildFileProperty.getPropertyValue());
				return builder.toString();
			}
			finally
			{
				StringBuilderSpinAllocator.dispose(builder);
			}
		}
	};

	public BuildFileProperty()
	{
		this("", "");
	}

	public BuildFileProperty(String propertyName, String propertyValue)
	{
		setPropertyName(propertyName);
		myPropertyValue = propertyValue;
	}

	@Attribute(NAME)
	public String getPropertyName()
	{
		return myPropertyName;
	}

	public void setPropertyName(String propertyName)
	{
		myPropertyName = propertyName.trim();
	}

	@Attribute(VALUE)
	public String getPropertyValue()
	{
		return myPropertyValue;
	}

	public void setPropertyValue(String propertyValue)
	{
		myPropertyValue = propertyValue;
	}

	public void readExternal(Element element)
	{
		myPropertyName = element.getAttributeValue(NAME);
		myPropertyValue = element.getAttributeValue(VALUE);
	}

	public void writeExternal(Element element)
	{
		element.setAttribute(NAME, getPropertyName());
		element.setAttribute(VALUE, getPropertyValue());
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;

		BuildFileProperty that = (BuildFileProperty) o;
		return Comparing.equal(myPropertyName, that.myPropertyName) && Comparing.equal(myPropertyValue, that.myPropertyValue);
	}

	@Override
	public int hashCode()
	{
		return 31 * (myPropertyName != null ? myPropertyName.hashCode() : 0) + (myPropertyValue != null ? myPropertyValue.hashCode() : 0);
	}
}
