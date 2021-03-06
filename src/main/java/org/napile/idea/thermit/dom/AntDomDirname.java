/*
 * Copyright 2000-2010 JetBrains s.r.o.
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
package org.napile.idea.thermit.dom;

import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author Eugene Zhuravlev
 *         Date: Apr 21, 2010
 */
public abstract class AntDomDirname extends AntDomPropertyDefiningTask
{
	@Attribute("file")
	@Convert(value = AntPathConverter.class)
	public abstract GenericAttributeValue<PsiFileSystemItem> getFile();

	@Nullable
	protected final String calcPropertyValue(String propertyName)
	{
		final PsiFileSystemItem fsItem = getFile().getValue();
		if(fsItem != null)
		{
			final PsiFileSystemItem parent = fsItem.getParent();
			if(parent != null)
			{
				final VirtualFile vFile = parent.getVirtualFile();
				if(vFile != null)
				{
					return FileUtil.toSystemDependentName(vFile.getPath());
				}
			}
		}
		// according to the doc, defaulting to project's current dir
		final String projectBasedirPath = getContextAntProject().getProjectBasedirPath();
		if(projectBasedirPath == null)
		{
			return null;
		}
		return FileUtil.toSystemDependentName(projectBasedirPath);
	}

}
