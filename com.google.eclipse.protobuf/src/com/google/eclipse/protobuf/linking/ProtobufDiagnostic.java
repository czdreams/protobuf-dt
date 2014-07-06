/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.linking;

import static java.util.Arrays.copyOf;

import static org.eclipse.xtext.util.Arrays.contains;

import static com.google.common.base.Objects.equal;

import org.eclipse.xtext.diagnostics.AbstractDiagnostic;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.nodemodel.INode;

import com.google.common.base.Objects;

/**
 * <code>{@link Diagnostic}</code> that supports appending text to its message.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufDiagnostic extends AbstractDiagnostic {
  private final String code;
  private final String[] data;
  private final StringBuilder message;
  private final INode node;

  public ProtobufDiagnostic(String code, String[] data, String message, INode node) {
    if (data == null) {
      throw new NullPointerException("The given array should not be null");
    }
    if (contains(data, null)) {
      throw new IllegalArgumentException("The given array should not contain null elements");
    }
    if (node == null) {
      throw new NullPointerException("The given node should not be null");
    }
    this.code = code;
    this.data = copyOf(data, data.length);
    this.message = new StringBuilder();
    this.message.append(message);
    this.node = node;
  }

  @Override public String getCode() {
    return code;
  }

  @Override public String[] getData() {
    return copyOf(data, data.length);
  }

  @Override public String getMessage() {
    return message.toString();
  }

  @Override protected INode getNode() {
    return node;
  }

  /**
   * Appends the given text to this diagnostic's message.
   * @param s the text to append.
   */
  public void appendToMessage(String s) {
    message.append(s);
  }

  @Override public int hashCode() {
    return Objects.hashCode(message, node);
  }

  @Override public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ProtobufDiagnostic other = (ProtobufDiagnostic) obj;
    if (!equal(message, other.message)) {
      return false;
    }
    return equal(node, other.node);
  }
}
