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
package org.napile.idea.thermit.config.impl.configuration;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;

import org.napile.idea.thermit.config.impl.AntBuildFileImpl;
import org.napile.idea.thermit.config.impl.AntInstallation;
import org.napile.idea.thermit.config.impl.AntReference;
import org.napile.idea.thermit.config.impl.GlobalThermitConfiguration;
import com.intellij.ui.ComboboxWithBrowseButton;
import com.intellij.util.config.AbstractProperty;
import com.intellij.util.containers.ConvertingIterator;

public class RunWithAntBinding extends UIPropertyBinding
{
	private final ArrayList<JComponent> myComponents = new ArrayList<JComponent>();
	private final JRadioButton myUseDefaultAnt;
	private final ComboboxWithBrowseButton myAnts;
	private final ChooseAndEditComboBoxController<AntReference, AntReference> myAntsController;
	private final GlobalThermitConfiguration myAntConfiguration;
	private boolean myEnabled = true;
	private boolean myLoadingValues = false;
	private JRadioButton myUseCustomAnt;

	public RunWithAntBinding(JRadioButton useDefaultAnt, JRadioButton useCastomAnt, ComboboxWithBrowseButton ants)
	{
		this(useDefaultAnt, useCastomAnt, ants, GlobalThermitConfiguration.getInstance());
	}

	RunWithAntBinding(JRadioButton useDefaultAnt, JRadioButton useCustomAnt, ComboboxWithBrowseButton ants, final GlobalThermitConfiguration antConfiguration)
	{
		myAntConfiguration = antConfiguration;
		myComponents.add(useDefaultAnt);
		myUseCustomAnt = useCustomAnt;
		myComponents.add(myUseCustomAnt);
		myAnts = ants;
		myUseDefaultAnt = useDefaultAnt;
		ButtonGroup group = new ButtonGroup();
		group.add(useDefaultAnt);
		group.add(myUseCustomAnt);

		myUseCustomAnt.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				updateEnableCombobox();
				if(myUseCustomAnt.isSelected() && !myLoadingValues)
					myAnts.getComboBox().requestFocusInWindow();
			}
		});

		myAntsController = new ChooseAndEditComboBoxController<AntReference, AntReference>(myAnts, new ConvertingIterator.IdConvertor<AntReference>(), AntReference.COMPARATOR)
		{
			public Iterator<AntReference> getAllListItems()
			{
				return antConfiguration.getConfiguredAnts().keySet().iterator();
			}

			public AntReference openConfigureDialog(AntReference reference, JComponent parent)
			{
				AntSetPanel antSetPanel = new AntSetPanel();
				AntInstallation installation = myAntConfiguration.getConfiguredAnts().get(reference);
				if(installation == null)
					installation = myAntConfiguration.getConfiguredAnts().get(AntReference.BUNDLED_ANT);
				antSetPanel.reset();
				antSetPanel.setSelection(installation);
				AntInstallation antInstallation = antSetPanel.showDialog(parent);
				return antInstallation != null ? antInstallation.getReference() : null;
			}
		};
		myAntsController.setRenderer(new AntUIUtil.AntReferenceRenderer(myAntConfiguration));
	}

	public void addAllPropertiesTo(Collection<AbstractProperty> properties)
	{
		properties.add(AntBuildFileImpl.ANT_REFERENCE);
	}

	public void apply(AbstractProperty.AbstractPropertyContainer container)
	{
		AntReference antReference = myUseDefaultAnt.isSelected() ? AntReference.PROJECT_DEFAULT : myAntsController.getSelectedItem();
		AntBuildFileImpl.ANT_REFERENCE.set(container, antReference);
	}

	public void beDisabled()
	{
		myEnabled = false;
		updateEnabled();
	}

	public void beEnabled()
	{
		myEnabled = true;
		updateEnabled();
	}

	public void loadValues(AbstractProperty.AbstractPropertyContainer container)
	{
		myLoadingValues = true;
		AntReference antReference = AntBuildFileImpl.ANT_REFERENCE.get(container);
		boolean isDefault = AntReference.PROJECT_DEFAULT == antReference;
		myUseDefaultAnt.setSelected(isDefault);
		myUseCustomAnt.setSelected(!isDefault);
		AntReference selection = isDefault ? null : antReference;
		myAntsController.resetList(selection);
		updateEnableCombobox();
		myLoadingValues = false;
	}

	private void updateEnabled()
	{
		for(JComponent component : myComponents)
		{
			component.setEnabled(myEnabled);
		}
		updateEnableCombobox();
	}

	private void updateEnableCombobox()
	{
		boolean enabled = myEnabled && myUseCustomAnt.isSelected();
		myAnts.setEnabled(enabled);
		if(!enabled)
			myAnts.getComboBox().setSelectedItem(null);
		if(!enabled || myLoadingValues)
			return;
		myAntsController.resetList(AntReference.BUNDLED_ANT);
	}
}
