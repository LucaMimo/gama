/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Statement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.Statement#getFunction <em>Function</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.Statement#getBlock <em>Block</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.Statement#getElse <em>Else</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.Statement#getFacets <em>Facets</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.Statement#getArgs <em>Args</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.Statement#getParams <em>Params</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.Statement#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getStatement()
 * @model
 * @generated
 */
public interface Statement extends GamlVarRef
{
  /**
   * Returns the value of the '<em><b>Function</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Function</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Function</em>' containment reference.
   * @see #setFunction(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getStatement_Function()
   * @model containment="true"
   * @generated
   */
  Expression getFunction();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Statement#getFunction <em>Function</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Function</em>' containment reference.
   * @see #getFunction()
   * @generated
   */
  void setFunction(Expression value);

  /**
   * Returns the value of the '<em><b>Block</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Block</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Block</em>' containment reference.
   * @see #setBlock(Block)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getStatement_Block()
   * @model containment="true"
   * @generated
   */
  Block getBlock();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Statement#getBlock <em>Block</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Block</em>' containment reference.
   * @see #getBlock()
   * @generated
   */
  void setBlock(Block value);

  /**
   * Returns the value of the '<em><b>Else</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Else</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Else</em>' containment reference.
   * @see #setElse(EObject)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getStatement_Else()
   * @model containment="true"
   * @generated
   */
  EObject getElse();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Statement#getElse <em>Else</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Else</em>' containment reference.
   * @see #getElse()
   * @generated
   */
  void setElse(EObject value);

  /**
   * Returns the value of the '<em><b>Facets</b></em>' containment reference list.
   * The list contents are of type {@link msi.gama.lang.gaml.gaml.Facet}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Facets</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Facets</em>' containment reference list.
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getStatement_Facets()
   * @model containment="true"
   * @generated
   */
  EList<Facet> getFacets();

  /**
   * Returns the value of the '<em><b>Args</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Args</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Args</em>' containment reference.
   * @see #setArgs(ActionArguments)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getStatement_Args()
   * @model containment="true"
   * @generated
   */
  ActionArguments getArgs();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Statement#getArgs <em>Args</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Args</em>' containment reference.
   * @see #getArgs()
   * @generated
   */
  void setArgs(ActionArguments value);

  /**
   * Returns the value of the '<em><b>Params</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Params</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Params</em>' containment reference.
   * @see #setParams(Parameters)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getStatement_Params()
   * @model containment="true"
   * @generated
   */
  Parameters getParams();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Statement#getParams <em>Params</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Params</em>' containment reference.
   * @see #getParams()
   * @generated
   */
  void setParams(Parameters value);

  /**
   * Returns the value of the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Value</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Value</em>' containment reference.
   * @see #setValue(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getStatement_Value()
   * @model containment="true"
   * @generated
   */
  Expression getValue();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Statement#getValue <em>Value</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Value</em>' containment reference.
   * @see #getValue()
   * @generated
   */
  void setValue(Expression value);

} // Statement
