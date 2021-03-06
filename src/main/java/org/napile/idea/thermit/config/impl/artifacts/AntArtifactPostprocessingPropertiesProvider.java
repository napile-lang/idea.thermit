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
package org.napile.idea.thermit.config.impl.artifacts;

import org.jetbrains.annotations.NotNull;
import com.intellij.packaging.artifacts.ArtifactProperties;
import com.intellij.packaging.artifacts.ArtifactPropertiesProvider;
import com.intellij.packaging.artifacts.ArtifactType;

/**
 * @author nik
 */
public class AntArtifactPostprocessingPropertiesProvider extends ArtifactPropertiesProvider
{
	public static AntArtifactPostprocessingPropertiesProvider getInstance()
	{
		return EP_NAME.findExtension(AntArtifactPostprocessingPropertiesProvider.class);
	}

	protected AntArtifactPostprocessingPropertiesProvider()
	{
		super("thermit-postprocessing");
	}

	@NotNull
	public ArtifactProperties<?> createProperties(@NotNull ArtifactType artifactType)
	{
		return new AntArtifactProperties(true);
	}

}
