/*
 * Copyright 2000-2011 JetBrains s.r.o.
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

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.napile.idea.thermit.ForcedAntFileAttribute;
import org.napile.idea.thermit.ThermitIcons;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;

/**
 * @author Eugene Zhuravlev
 *         Date: Apr 6, 2010
 */
public class AntDomFileDescription extends AntFileDescription<AntDomProject>
{
	private static final String ROOT_TAG_NAME = "project";
	private static final String EXTENSION = "txml";

	public AntDomFileDescription()
	{
		super(AntDomProject.class, ROOT_TAG_NAME);
	}

	@Override
	public Icon getFileIcon(@Iconable.IconFlags int flags)
	{
		return ThermitIcons.FILE_ICON;
	}

	@Override
	public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module)
	{
		return super.isMyFile(file, module) && isAntFile(file) && EXTENSION.equals(file.getVirtualFile().getExtension());
	}

	public static boolean isAntFile(final XmlFile xmlFile)
	{
		final XmlDocument document = xmlFile.getDocument();
		if(document != null)
		{
			final XmlTag tag = document.getRootTag();
			final VirtualFile vFile = xmlFile.getOriginalFile().getVirtualFile();
			if(tag != null && ROOT_TAG_NAME.equals(tag.getName()) && tag.getContext() instanceof XmlDocument)
			{
				if(tag.getAttributeValue("name") != null && tag.getAttributeValue("default") != null && vFile != null && ForcedAntFileAttribute.mayBeAntFile(vFile))
				{
					return true;
				}
			}
			if(vFile != null && ForcedAntFileAttribute.isAntFile(vFile))
			{
				return true;
			}
		}
		return false;
	}

}
