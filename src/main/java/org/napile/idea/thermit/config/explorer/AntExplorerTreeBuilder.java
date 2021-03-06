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
package org.napile.idea.thermit.config.explorer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jetbrains.annotations.NotNull;
import org.napile.idea.thermit.config.AntBuildFile;
import org.napile.idea.thermit.config.AntBuildFileBase;
import org.napile.idea.thermit.config.AntConfigurationListener;
import org.napile.idea.thermit.config.ThermitConfiguration;
import com.intellij.ide.util.treeView.AbstractTreeBuilder;
import com.intellij.ide.util.treeView.AbstractTreeUi;
import com.intellij.ide.util.treeView.IndexComparator;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.ide.util.treeView.TreeBuilderUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.util.ProgressIndicatorBase;
import com.intellij.openapi.progress.util.ProgressIndicatorUtils;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.tree.TreeUtil;

final class AntExplorerTreeBuilder extends AbstractTreeBuilder
{

	private static final TreePath[] EMPTY_TREE_PATH = new TreePath[0];
	private final AntConfigurationListener myAntBuildListener;
	private final Project myProject;
	private ThermitConfiguration myConfig;
	private ExpandedStateUpdater myExpansionListener;

	public AntExplorerTreeBuilder(Project project, JTree tree, DefaultTreeModel treeModel)
	{
		super(tree, treeModel, new AntExplorerTreeStructure(project), IndexComparator.INSTANCE);
		myProject = project;
		myAntBuildListener = new ConfigurationListener();
		myConfig = ThermitConfiguration.getInstance(myProject);
		myConfig.addAntConfigurationListener(myAntBuildListener);
		myExpansionListener = new ExpandedStateUpdater();
		tree.addTreeExpansionListener(myExpansionListener);
		initRootNode();
	}


	public void dispose()
	{
		final ThermitConfiguration config = myConfig;
		if(config != null)
		{
			config.removeAntConfigurationListener(myAntBuildListener);
			myConfig = null;
		}

		final ExpandedStateUpdater expansionListener = myExpansionListener;
		final JTree tree = getTree();
		if(expansionListener != null && tree != null)
		{
			tree.removeTreeExpansionListener(expansionListener);
			myExpansionListener = null;
		}

		super.dispose();
	}

	protected boolean isAlwaysShowPlus(NodeDescriptor nodeDescriptor)
	{
		return false;
	}

	protected boolean isAutoExpandNode(NodeDescriptor nodeDescriptor)
	{
		return ((AntNodeDescriptor) nodeDescriptor).isAutoExpand();
	}

	public void setTargetsFiltered(boolean value)
	{
		((AntExplorerTreeStructure) getTreeStructure()).setFilteredTargets(value);
		queueUpdate();
	}

	@NotNull
	protected ProgressIndicator createProgressIndicator()
	{
		return ProgressIndicatorUtils.forceWriteActionPriority(new ProgressIndicatorBase(), this);
	}

	private final class ConfigurationListener implements AntConfigurationListener
	{
		public void configurationLoaded()
		{
			queueUpdate();
		}

		public void buildFileAdded(AntBuildFile buildFile)
		{
			queueUpdate();
		}

		public void buildFileChanged(AntBuildFile buildFile)
		{
			queueUpdateFrom(buildFile, false);
		}

		public void buildFileRemoved(AntBuildFile buildFile)
		{
			queueUpdate();
		}
	}

	public void expandAll()
	{
		final List<Object> pathsToExpand = new ArrayList<Object>();
		final List<Object> selectionPaths = new ArrayList<Object>();
		TreeBuilderUtil.storePaths(this, getRootNode(), pathsToExpand, selectionPaths, true);
		int row = 0;
		while(row < getTree().getRowCount())
		{
			getTree().expandRow(row);
			row++;
		}
		getTree().setSelectionPaths(EMPTY_TREE_PATH);
		TreeBuilderUtil.restorePaths(this, pathsToExpand, selectionPaths, true);
	}

	void collapseAll()
	{
		final List<Object> pathsToExpand = new ArrayList<Object>();
		final List<Object> selectionPaths = new ArrayList<Object>();
		TreeBuilderUtil.storePaths(this, getRootNode(), pathsToExpand, selectionPaths, true);
		TreeUtil.collapseAll(getTree(), 1);
		getTree().setSelectionPaths(EMPTY_TREE_PATH);
		pathsToExpand.clear();
		TreeBuilderUtil.restorePaths(this, pathsToExpand, selectionPaths, true);
	}

	private class ExpandedStateUpdater implements TreeExpansionListener
	{
		public void treeExpanded(TreeExpansionEvent event)
		{
			setExpandedState(event, true);
		}

		public void treeCollapsed(TreeExpansionEvent event)
		{
			setExpandedState(event, false);
		}

		private void setExpandedState(TreeExpansionEvent event, boolean shouldExpand)
		{
			final TreePath path = event.getPath();
			final AbstractTreeUi ui = getUi();
			final Object lastPathComponent = path.getLastPathComponent();
			if(lastPathComponent != null)
			{
				final Object element = ui.getElementFor(lastPathComponent);
				if(element instanceof AntBuildFileBase)
				{
					((AntBuildFileBase) element).setShouldExpand(shouldExpand);
				}
			}
		}
	}
}
