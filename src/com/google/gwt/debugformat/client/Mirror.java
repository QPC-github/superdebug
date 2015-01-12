package com.google.gwt.debugformat.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Provides reflective access to a Java or JavaScript object, for display in the debugger.
 * The default implementation displays if it's a generic Java object.
 */
class Mirror {

  /**
   * Returns true if this mirror can display the given object.
   */
  boolean canDisplay(Any any) {
    return any.toJava() != null;
  }

  /**
   * Returns a single-line summary of the object, to be displayed before it's expanded.
   */
  String getHeader(Any any) {
    return any.toJava().getClassName() + " (Java)";
  }

  boolean hasChildren(Any any) {
    return any.toJava().hasFields();
  }

  Page getChildren(Any any) {
    return new Page(any.toJava().getFields(), 0);
  }

  /**
   * Used to page through large lists of children.
   */
  static class Page {
    // The number of children to display before a "More" prompt.
    static final int CHILDREN_PER_PAGE = 100;

    private final Mirror.Children children;
    private final int start;

    Page(Mirror.Children children, int start) {
      this.children = children;
      this.start = start;
    }

    int firstIndex() {
      return start;
    }

    int lastIndex() {
      return firstIndex() + length() - 1;
    }

    int length() {
      int remaining = children.length() - start;
      return remaining > CHILDREN_PER_PAGE ? CHILDREN_PER_PAGE : remaining;
    }

    Child get(int index) {
      return children.get(index + start);
    }

    Page nextPage() {
      int remaining = children.length() - start;
      if (remaining <= CHILDREN_PER_PAGE) {
        return null;
      }
      return new Page(children, start + CHILDREN_PER_PAGE);
    }
  }

  /**
   * A list of the children of a node in the debugger.
   */
  static class Children extends JsArray<Child> {
    protected Children() {}

    static native Children create() /*-{
      return [];
    }-*/;

    final void add(String name, Any value) {
      push(Child.create(name, value));
    }
  }

  /**
   * Represents a child node in the debugger.
   */
  static class Child extends JavaScriptObject {
    protected Child() {}

    static native Child create(String name, Any value) /*-{
      return {name: name, value: value};
    }-*/;

    final native String getName() /*-{
      return this.name;
    }-*/;

    final native Any getValue() /*-{
      return this.value;
    }-*/;
  }
}
