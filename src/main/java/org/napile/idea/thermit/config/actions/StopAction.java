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
package org.napile.idea.thermit.config.actions;

import com.intellij.icons.AllIcons;
import org.napile.idea.thermit.AntBundle;
import org.napile.idea.thermit.config.execution.AntBuildMessageView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;

public final class StopAction extends AnAction {
  private final AntBuildMessageView myAntBuildMessageView;

  public StopAction(AntBuildMessageView antBuildMessageView) {
    super(AntBundle.message("stop.ant.action.name"),null, AllIcons.Actions.Suspend);
    myAntBuildMessageView = antBuildMessageView;
  }

  public void actionPerformed(AnActionEvent e) {
    myAntBuildMessageView.stopProcess();
  }

  public void update(AnActionEvent event){
    Presentation presentation = event.getPresentation();
    presentation.setEnabled(!myAntBuildMessageView.isStopped());
  }
}