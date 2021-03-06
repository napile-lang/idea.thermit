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
package org.napile.idea.thermit.validation;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.napile.idea.thermit.ThermitBundle;
import org.napile.idea.thermit.dom.AntDomProject;
import org.napile.idea.thermit.dom.AntDomTarget;
import org.napile.idea.thermit.dom.TargetResolver;
import com.intellij.openapi.util.Comparing;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;

public class AntDuplicateTargetsInspection extends AntInspection
{

	@NonNls
	private static final String SHORT_NAME = "AntDuplicateTargetsInspection";

	@Nls
	@NotNull
	public String getDisplayName()
	{
		return ThermitBundle.message("ant.duplicate.targets.inspection");
	}

	@NonNls
	@NotNull
	public String getShortName()
	{
		return SHORT_NAME;
	}

	protected void checkDomElement(DomElement element, final DomElementAnnotationHolder holder, DomHighlightingHelper helper)
	{
		if(element instanceof AntDomProject)
		{
			final AntDomProject project = (AntDomProject) element;
			TargetResolver.validateDuplicateTargets(project.getContextAntProject(), new TargetResolver.TargetSink()
			{
				public void duplicateTargetDetected(AntDomTarget existingTarget, AntDomTarget duplicatingTarget, String targetEffectiveName)
				{
					final AntDomProject existingTargetProj = existingTarget.getAntProject();
					final AntDomProject duplucatingTargetProj = duplicatingTarget.getAntProject();
					final boolean isFromDifferentFiles = !Comparing.equal(existingTargetProj, duplucatingTargetProj);
					if(project.equals(existingTargetProj))
					{
						final String duplicatedMessage = isFromDifferentFiles ? ThermitBundle.message("target.is.duplicated.in.imported.file", targetEffectiveName, duplucatingTargetProj != null ? duplucatingTargetProj.getName() : "") : ThermitBundle.message("target.is.duplicated", targetEffectiveName);
						holder.createProblem(existingTarget.getName(), duplicatedMessage);
					}
					if(project.equals(duplucatingTargetProj))
					{
						final String duplicatedMessage = isFromDifferentFiles ? ThermitBundle.message("target.is.duplicated.in.imported.file", targetEffectiveName, existingTargetProj != null ? existingTargetProj.getName() : "") : ThermitBundle.message("target.is.duplicated", targetEffectiveName);
						holder.createProblem(duplicatingTarget.getName(), duplicatedMessage);
					}
				}
			});
		}
	}
}
