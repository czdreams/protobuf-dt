/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

import static org.eclipse.xtext.util.Strings.isEmpty;

import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.browseCustomPath;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.compileOnSave;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.descriptorLocation;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.errorInvalidDescriptor;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.errorInvalidProtoc;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.errorInvalidAsProtoc;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.errorNoLanguageSelected;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.errorNoSelection;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.protocInCustomPath;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.protocInSystemPath;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.protocLocation;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.refreshOutputProject;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.refreshProject;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.refreshResources;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.tabMain;
import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.tabRefresh;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.COMPILE_PROTO_FILES;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.CPP_CODE_GENERATION_ENABLED;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.CPP_OUTPUT_DIRECTORY;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.DESCRIPTOR_FILE_PATH;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.JAVA_CODE_GENERATION_ENABLED;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.JAVA_OUTPUT_DIRECTORY;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.PROTOC_FILE_PATH;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.PYTHON_CODE_GENERATION_ENABLED;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.PYTHON_OUTPUT_DIRECTORY;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.AS_CODE_GENERATION_ENABLED;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.AS_OUTPUT_DIRECTORY;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.AS_PROTOC_FILE_PATH;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.REFRESH_OUTPUT_DIRECTORY;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.REFRESH_PROJECT;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.REFRESH_RESOURCES;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.USE_PROTOC_IN_CUSTOM_PATH;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.USE_PROTOC_IN_SYSTEM_PATH;
import static com.google.eclipse.protobuf.ui.preferences.pages.ButtonGroup.with;
import static com.google.eclipse.protobuf.ui.preferences.pages.LabelWidgets.setEnabled;
import static com.google.eclipse.protobuf.ui.preferences.pages.TextWidgets.setEditable;
import static com.google.eclipse.protobuf.ui.preferences.pages.TextWidgets.setEnabled;
import static com.google.eclipse.protobuf.ui.preferences.pages.binding.BindingToButtonSelection.bindSelectionOf;
import static com.google.eclipse.protobuf.ui.preferences.pages.binding.BindingToTextValue.bindTextOf;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.google.eclipse.protobuf.ui.preferences.pages.PreferenceAndPropertyPage;
import com.google.eclipse.protobuf.ui.preferences.pages.binding.PreferenceBinder;
import com.google.eclipse.protobuf.ui.preferences.pages.binding.PreferenceFactory;

/**
 * Preference page for protobuf compiler.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompilerPreferencePage extends PreferenceAndPropertyPage {
  private static final String PREFERENCE_PAGE_ID = CompilerPreferencePage.class.getName();

  private Button btnCompileProtoFiles;
  private TabFolder tabFolder;
  private TabItem tbtmMain;
  private TabItem tbtmRefresh;
  private TabItem tbtmOptions;
  private Group grpCompilerLocation;
  private Button btnUseProtocInSystemPath;
  private Button btnUseProtocInCustomPath;
  private Text txtProtocFilePath;
  private Button btnProtocPathBrowse;
  private Text txtAsProtocFilePath;
  private Button btnAsProtocPathBrowse;
  private Group grpDescriptorLocation;
  private Text txtDescriptorFilePath;
  private Button btnDescriptorPathBrowse;
  private Button btnGenerateJava;
  private Label lblJavaOutputDirectory;
  private Text txtJavaOutputDirectory;
  private Button btnGenerateCpp;
  private Label lblCppOutputDirectory;
  private Text txtCppOutputDirectory;
  private Button btnGeneratePython;
  private Label lblPythonOutputDirectory;
  private Text txtPythonOutputDirectory;
  private Button btnGenerateAs;
  private Label lblAsOutputDirectory;
  private Text txtAsOutputDirectory;
  private Group grpRefresh;
  private Button btnRefreshResources;
  private Button btnRefreshProject;
  private Button btnRefreshOutputDirectory;

  @Override protected void doCreateContents(Composite parent) {
    btnCompileProtoFiles = new Button(parent, SWT.CHECK);
    btnCompileProtoFiles.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
    btnCompileProtoFiles.setText(compileOnSave);

    tabFolder = new TabFolder(parent, SWT.NONE);
    tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

    tbtmMain = new TabItem(tabFolder, SWT.NONE);
    tbtmMain.setText(tabMain);

    Composite cmpMain = new Composite(tabFolder, SWT.NONE);
    tbtmMain.setControl(cmpMain);
    cmpMain.setLayout(new GridLayout(1, false));

    grpCompilerLocation = new Group(cmpMain, SWT.NONE);
    grpCompilerLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    grpCompilerLocation.setLayout(new GridLayout(2, false));
    grpCompilerLocation.setText(protocLocation);

    btnUseProtocInSystemPath = new Button(grpCompilerLocation, SWT.RADIO);
    btnUseProtocInSystemPath.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
    btnUseProtocInSystemPath.setText(protocInSystemPath);

    btnUseProtocInCustomPath = new Button(grpCompilerLocation, SWT.RADIO);
    btnUseProtocInCustomPath.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
    btnUseProtocInCustomPath.setText(protocInCustomPath);

    txtProtocFilePath = new Text(grpCompilerLocation, SWT.BORDER);
    txtProtocFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    setEditable(txtProtocFilePath, false);

    btnProtocPathBrowse = new Button(grpCompilerLocation, SWT.NONE);
    btnProtocPathBrowse.setText(browseCustomPath);

    txtAsProtocFilePath = new Text(grpCompilerLocation, SWT.BORDER);
    txtAsProtocFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    setEditable(txtAsProtocFilePath, false);
    
    btnAsProtocPathBrowse = new Button(grpCompilerLocation, SWT.NONE);
    btnAsProtocPathBrowse.setText(browseCustomPath);
    
    grpDescriptorLocation = new Group(cmpMain, SWT.NONE);
    grpDescriptorLocation.setText(descriptorLocation);
    grpDescriptorLocation.setLayout(new GridLayout(2, false));
    grpDescriptorLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    txtDescriptorFilePath = new Text(grpDescriptorLocation, SWT.BORDER);
    txtDescriptorFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    setEditable(txtDescriptorFilePath, false);

    btnDescriptorPathBrowse = new Button(grpDescriptorLocation, SWT.NONE);
    btnDescriptorPathBrowse.setText(browseCustomPath);

    tbtmOptions = new TabItem(tabFolder, SWT.NONE);
    tbtmOptions.setText("&Options");

    Composite cmpOptions = new Composite(tabFolder, SWT.NONE);
    tbtmOptions.setControl(cmpOptions);
    cmpOptions.setLayout(new GridLayout(2, false));

    btnGenerateJava = new Button(cmpOptions, SWT.CHECK);
    btnGenerateJava.setEnabled(false);
    btnGenerateJava.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
    btnGenerateJava.setText("Generate Java");

    lblJavaOutputDirectory = new Label(cmpOptions, SWT.NONE);
    lblJavaOutputDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblJavaOutputDirectory.setText("Java Output Directory:");

    txtJavaOutputDirectory = new Text(cmpOptions, SWT.BORDER);
    txtJavaOutputDirectory.setEnabled(false);
    txtJavaOutputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    btnGenerateCpp = new Button(cmpOptions, SWT.CHECK);
    btnGenerateCpp.setEnabled(false);
    btnGenerateCpp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
    btnGenerateCpp.setText("Generate C++");

    lblCppOutputDirectory = new Label(cmpOptions, SWT.NONE);
    lblCppOutputDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblCppOutputDirectory.setText("C++ Output Directory:");

    txtCppOutputDirectory = new Text(cmpOptions, SWT.BORDER);
    txtCppOutputDirectory.setEnabled(false);
    txtCppOutputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

    btnGeneratePython = new Button(cmpOptions, SWT.CHECK);
    btnGeneratePython.setEnabled(false);
    btnGeneratePython.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
    btnGeneratePython.setText("Generate Python");

    lblPythonOutputDirectory = new Label(cmpOptions, SWT.NONE);
    lblPythonOutputDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblPythonOutputDirectory.setText("Python Output Directory:");

    txtPythonOutputDirectory = new Text(cmpOptions, SWT.BORDER);
    txtPythonOutputDirectory.setEnabled(false);
    txtPythonOutputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

    btnGenerateAs = new Button(cmpOptions, SWT.CHECK);
    btnGenerateAs.setEnabled(false);
    btnGenerateAs.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
    btnGenerateAs.setText("Generate As");

    lblAsOutputDirectory = new Label(cmpOptions, SWT.NONE);
    lblAsOutputDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblAsOutputDirectory.setText("As Output Directory:");

    txtAsOutputDirectory = new Text(cmpOptions, SWT.BORDER);
    txtAsOutputDirectory.setEnabled(false);
    txtAsOutputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    
    tbtmRefresh = new TabItem(tabFolder, SWT.NONE);
    tbtmRefresh.setText(tabRefresh);

    Composite cmpRefresh = new Composite(tabFolder, SWT.NONE);
    tbtmRefresh.setControl(cmpRefresh);
    cmpRefresh.setLayout(new GridLayout(1, false));

    btnRefreshResources = new Button(cmpRefresh, SWT.CHECK);
    btnRefreshResources.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    btnRefreshResources.setText(refreshResources);

    grpRefresh = new Group(cmpRefresh, SWT.NONE);
    grpRefresh.setLayout(new GridLayout(1, false));
    grpRefresh.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    btnRefreshProject = new Button(grpRefresh, SWT.RADIO);
    btnRefreshProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    btnRefreshProject.setText(refreshProject);

    btnRefreshOutputDirectory = new Button(grpRefresh, SWT.RADIO);
    btnRefreshOutputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    btnRefreshOutputDirectory.setText(refreshOutputProject);
    new Label(parent, SWT.NONE);

    addEventListeners();
  }

  private void addEventListeners() {
    btnCompileProtoFiles.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        boolean selected = btnCompileProtoFiles.getSelection();
        enableCompilerSettings(selected);
        checkState();
      }
    });
    with(btnUseProtocInCustomPath, btnUseProtocInSystemPath).add(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        boolean selected = btnUseProtocInCustomPath.getSelection();
        enableCompilerCustomPathSettings(selected);
        checkState();
      }
    });
    btnProtocPathBrowse.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
        String file = dialog.open();
        if (file != null) {
          txtProtocFilePath.setText(file);
        }
        checkState();
      }
    });
    btnAsProtocPathBrowse.addSelectionListener(new SelectionAdapter() {
        @Override public void widgetSelected(SelectionEvent e) {
          FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
          String file = dialog.open();
          if (file != null) {
            txtAsProtocFilePath.setText(file);
          }
          checkState();
        }
      });
    btnDescriptorPathBrowse.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
        dialog.setFilterExtensions(new String[] { "*.proto" });
        String file = dialog.open();
        if (file != null) {
          txtDescriptorFilePath.setText(file);
        }
        checkState();
      }
    });
    btnGenerateJava.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        enableJavaOutputDirectory(btnGenerateJava.getSelection());
        checkState();
      }
    });
    btnGenerateCpp.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        enableCppOutputDirectory(btnGenerateCpp.getSelection());
        checkState();
      }
    });
    btnGeneratePython.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        enablePythonOutputDirectory(btnGeneratePython.getSelection());
        checkState();
      }
    });
    btnGenerateAs.addSelectionListener(new SelectionAdapter() {
        @Override public void widgetSelected(SelectionEvent e) {
          enableAsOutputDirectory(btnGenerateAs.getSelection());
          checkState();
        }
      });
    btnRefreshResources.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        refreshResourcesSettingsEnabled(btnRefreshResources.getSelection());
      }
    });
  }

  @Override protected String enableProjectSettingsPreferenceName() {
    return ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME;
  }

  @Override protected void setupBinding(PreferenceBinder binder, PreferenceFactory factory) {
    binder.addAll(
        bindSelectionOf(btnCompileProtoFiles).to(factory.newBooleanPreference(COMPILE_PROTO_FILES)),
        bindSelectionOf(btnUseProtocInSystemPath).to(factory.newBooleanPreference(USE_PROTOC_IN_SYSTEM_PATH)),
        bindSelectionOf(btnUseProtocInCustomPath).to(factory.newBooleanPreference(USE_PROTOC_IN_CUSTOM_PATH)),
        bindTextOf(txtProtocFilePath).to(factory.newStringPreference(PROTOC_FILE_PATH)),
        bindTextOf(txtAsProtocFilePath).to(factory.newStringPreference(AS_PROTOC_FILE_PATH)),
        bindTextOf(txtDescriptorFilePath).to(factory.newStringPreference(DESCRIPTOR_FILE_PATH)),
        bindSelectionOf(btnGenerateJava).to(factory.newBooleanPreference(JAVA_CODE_GENERATION_ENABLED)),
        bindTextOf(txtJavaOutputDirectory).to(factory.newStringPreference(JAVA_OUTPUT_DIRECTORY)),
        bindSelectionOf(btnGenerateCpp).to(factory.newBooleanPreference(CPP_CODE_GENERATION_ENABLED)),
        bindTextOf(txtCppOutputDirectory).to(factory.newStringPreference(CPP_OUTPUT_DIRECTORY)),
        bindSelectionOf(btnGeneratePython).to(factory.newBooleanPreference(PYTHON_CODE_GENERATION_ENABLED)),
        bindTextOf(txtPythonOutputDirectory).to(factory.newStringPreference(PYTHON_OUTPUT_DIRECTORY)),
        bindSelectionOf(btnGenerateAs).to(factory.newBooleanPreference(AS_CODE_GENERATION_ENABLED)),
        bindTextOf(txtAsOutputDirectory).to(factory.newStringPreference(AS_OUTPUT_DIRECTORY)),
        bindSelectionOf(btnRefreshResources).to(factory.newBooleanPreference(REFRESH_RESOURCES)),
        bindSelectionOf(btnRefreshProject).to(factory.newBooleanPreference(REFRESH_PROJECT)),
        bindSelectionOf(btnRefreshOutputDirectory).to(factory.newBooleanPreference(REFRESH_OUTPUT_DIRECTORY))
      );
  }

  @Override protected void updateContents() {
    boolean compileProtoFiles = btnCompileProtoFiles.getSelection();
    boolean shouldEnableCompilerOptions = compileProtoFiles;
    if (isPropertyPage()) {
      boolean useProjectSettings = areProjectSettingsActive();
      activateProjectSettings(useProjectSettings);
      enableProjectSpecificSettings(useProjectSettings);
      shouldEnableCompilerOptions = shouldEnableCompilerOptions && useProjectSettings;
    }
    enableCompilerSettings(shouldEnableCompilerOptions);
  }

  @Override protected void onProjectSettingsActivation(boolean active) {
    enableProjectSpecificSettings(active);
    enableCompilerSettings(isEnabledAndSelected(btnCompileProtoFiles));
    checkState();
  }

  private void enableProjectSpecificSettings(boolean enabled) {
    btnCompileProtoFiles.setEnabled(enabled);
  }

  private void enableCompilerSettings(boolean enabled) {
    enableCompilerPathSettings(enabled);
    enableDescriptorPathSettings(enabled);
    enableOptionsSettings(enabled);
    enableRefreshSettings(enabled);
  }

  private void enableCompilerPathSettings(boolean enabled) {
    grpCompilerLocation.setEnabled(enabled);
    btnUseProtocInSystemPath.setEnabled(enabled);
    btnUseProtocInCustomPath.setEnabled(enabled);
    enableCompilerCustomPathSettings(customPathOptionSelectedAndEnabled());
  }

  private void enableCompilerCustomPathSettings(boolean enabled) {
    txtProtocFilePath.setEnabled(enabled);
    btnProtocPathBrowse.setEnabled(enabled);
    txtAsProtocFilePath.setEnabled(enabled);
    btnAsProtocPathBrowse.setEnabled(enabled);
  }

  private void enableDescriptorPathSettings(boolean enabled) {
    grpDescriptorLocation.setEnabled(enabled);
    txtDescriptorFilePath.setEnabled(enabled);
    btnDescriptorPathBrowse.setEnabled(enabled);
  }

  private boolean customPathOptionSelectedAndEnabled() {
    return isEnabledAndSelected(btnUseProtocInCustomPath);
  }

  private void enableOptionsSettings(boolean enabled) {
    btnGenerateJava.setEnabled(enabled);
    enableJavaOutputDirectory(isEnabledAndSelected(btnGenerateJava));
    btnGenerateCpp.setEnabled(enabled);
    enableCppOutputDirectory(isEnabledAndSelected(btnGenerateCpp));
    btnGeneratePython.setEnabled(enabled);
    enablePythonOutputDirectory(isEnabledAndSelected(btnGeneratePython));
    btnGenerateAs.setEnabled(enabled);
    enableAsOutputDirectory(isEnabledAndSelected(btnGenerateAs));
  }

  private void enableJavaOutputDirectory(boolean enabled) {
    setEnabled(txtJavaOutputDirectory, enabled);
    setEnabled(lblJavaOutputDirectory, enabled);
  }

  private void enableCppOutputDirectory(boolean enabled) {
    setEnabled(txtCppOutputDirectory, enabled);
    setEnabled(lblCppOutputDirectory, enabled);
  }

  private void enablePythonOutputDirectory(boolean enabled) {
    setEnabled(txtPythonOutputDirectory, enabled);
    setEnabled(lblPythonOutputDirectory, enabled);
  }

  private void enableAsOutputDirectory(boolean enabled) {
    setEnabled(txtAsOutputDirectory, enabled);
	setEnabled(lblAsOutputDirectory, enabled);
  }

  private void enableRefreshSettings(boolean enabled) {
    btnRefreshResources.setEnabled(enabled);
    refreshResourcesSettingsEnabled(isEnabledAndSelected(btnRefreshResources));
  }

  private boolean isEnabledAndSelected(Button button) {
    return button.isEnabled() && button.getSelection();
  }

  private void refreshResourcesSettingsEnabled(boolean enabled) {
    grpRefresh.setEnabled(enabled);
    btnRefreshProject.setEnabled(enabled);
    btnRefreshOutputDirectory.setEnabled(enabled);
  }

  private void checkState() {
    if (isPropertyPage() && !areProjectSettingsActive()) {
      // the page is a 'project property' page and the 'enable project settings' check-box is not selected
      pageIsNowValid();
      return;
    }
    if (!btnCompileProtoFiles.getSelection()) {
      // all the options of this page are disabled
      pageIsNowValid();
      return;
    }
    if (!atLeastOneTargetLanguageIsSelected()) {
      pageIsNowInvalid(errorNoLanguageSelected);
      return;
    }
    if (customPathOptionSelectedAndEnabled()) {
      String protocPath = txtProtocFilePath.getText();
      if (isEmpty(protocPath)) {
        pageIsNowInvalid(errorNoSelection);
        return;
      }
      File protoc = new File(protocPath);
      if (!protoc.isFile()) {
        pageIsNowInvalid(errorInvalidProtoc);
        return;
      }
      if (btnGenerateAs.getSelection()) {
          String asProtocPath = txtAsProtocFilePath.getText();
          if (isEmpty(asProtocPath)) {
            pageIsNowInvalid(errorNoSelection);
            return;
          }
          File asProtoc = new File(asProtocPath);
          if (!asProtoc.isFile()) {
            pageIsNowInvalid(errorInvalidAsProtoc);
            return;
          }
      }
    }
    String descriptorPath = txtDescriptorFilePath.getText();
    if (!isEmpty(descriptorPath) && !isFileWithName(descriptorPath, "descriptor.proto")) {
      pageIsNowInvalid(errorInvalidDescriptor);
      return;
    }
    pageIsNowValid();
  }

  private boolean atLeastOneTargetLanguageIsSelected() {
    return btnGenerateJava.getSelection() || btnGenerateCpp.getSelection() || btnGeneratePython.getSelection() || btnGenerateAs.getSelection();
  }

  private boolean isFileWithName(String filePath, String expectedFileName) {
    File file = new File(filePath);
    if (!file.isFile()) {
      return false;
    }
    String fileName = file.getName();
    return expectedFileName.equals(fileName);
  }

  @Override protected String preferencePageId() {
    return PREFERENCE_PAGE_ID;
  }
}
