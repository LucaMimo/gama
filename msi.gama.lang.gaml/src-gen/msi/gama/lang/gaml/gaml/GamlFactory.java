/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see msi.gama.lang.gaml.gaml.GamlPackage
 * @generated
 */
public interface GamlFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  GamlFactory eINSTANCE = msi.gama.lang.gaml.gaml.impl.GamlFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Model</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Model</em>'.
   * @generated
   */
  Model createModel();

  /**
   * Returns a new object of class '<em>Import</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Import</em>'.
   * @generated
   */
  Import createImport();

  /**
   * Returns a new object of class '<em>Statement</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Statement</em>'.
   * @generated
   */
  Statement createStatement();

  /**
   * Returns a new object of class '<em>Contents</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Contents</em>'.
   * @generated
   */
  Contents createContents();

  /**
   * Returns a new object of class '<em>Parameters</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Parameters</em>'.
   * @generated
   */
  Parameters createParameters();

  /**
   * Returns a new object of class '<em>Action Arguments</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Action Arguments</em>'.
   * @generated
   */
  ActionArguments createActionArguments();

  /**
   * Returns a new object of class '<em>Argument Definition</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Argument Definition</em>'.
   * @generated
   */
  ArgumentDefinition createArgumentDefinition();

  /**
   * Returns a new object of class '<em>Facet</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Facet</em>'.
   * @generated
   */
  Facet createFacet();

  /**
   * Returns a new object of class '<em>Block</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Block</em>'.
   * @generated
   */
  Block createBlock();

  /**
   * Returns a new object of class '<em>Expression</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Expression</em>'.
   * @generated
   */
  Expression createExpression();

  /**
   * Returns a new object of class '<em>Argument Pair</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Argument Pair</em>'.
   * @generated
   */
  ArgumentPair createArgumentPair();

  /**
   * Returns a new object of class '<em>Expression List</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Expression List</em>'.
   * @generated
   */
  ExpressionList createExpressionList();

  /**
   * Returns a new object of class '<em>Variable Ref</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Variable Ref</em>'.
   * @generated
   */
  VariableRef createVariableRef();

  /**
   * Returns a new object of class '<em>Var Ref</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Var Ref</em>'.
   * @generated
   */
  GamlVarRef createGamlVarRef();

  /**
   * Returns a new object of class '<em>Terminal Expression</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Terminal Expression</em>'.
   * @generated
   */
  TerminalExpression createTerminalExpression();

  /**
   * Returns a new object of class '<em>String Evaluator</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>String Evaluator</em>'.
   * @generated
   */
  StringEvaluator createStringEvaluator();

  /**
   * Returns a new object of class '<em>Pair</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Pair</em>'.
   * @generated
   */
  Pair createPair();

  /**
   * Returns a new object of class '<em>If</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>If</em>'.
   * @generated
   */
  If createIf();

  /**
   * Returns a new object of class '<em>Binary</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Binary</em>'.
   * @generated
   */
  Binary createBinary();

  /**
   * Returns a new object of class '<em>Unit</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Unit</em>'.
   * @generated
   */
  Unit createUnit();

  /**
   * Returns a new object of class '<em>Unary</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Unary</em>'.
   * @generated
   */
  Unary createUnary();

  /**
   * Returns a new object of class '<em>Access</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Access</em>'.
   * @generated
   */
  Access createAccess();

  /**
   * Returns a new object of class '<em>Dot</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Dot</em>'.
   * @generated
   */
  Dot createDot();

  /**
   * Returns a new object of class '<em>Array</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Array</em>'.
   * @generated
   */
  Array createArray();

  /**
   * Returns a new object of class '<em>Point</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Point</em>'.
   * @generated
   */
  Point createPoint();

  /**
   * Returns a new object of class '<em>Function</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Function</em>'.
   * @generated
   */
  Function createFunction();

  /**
   * Returns a new object of class '<em>Parameter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Parameter</em>'.
   * @generated
   */
  Parameter createParameter();

  /**
   * Returns a new object of class '<em>Unit Name</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Unit Name</em>'.
   * @generated
   */
  UnitName createUnitName();

  /**
   * Returns a new object of class '<em>Int Literal</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Int Literal</em>'.
   * @generated
   */
  IntLiteral createIntLiteral();

  /**
   * Returns a new object of class '<em>Double Literal</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Double Literal</em>'.
   * @generated
   */
  DoubleLiteral createDoubleLiteral();

  /**
   * Returns a new object of class '<em>Color Literal</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Color Literal</em>'.
   * @generated
   */
  ColorLiteral createColorLiteral();

  /**
   * Returns a new object of class '<em>String Literal</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>String Literal</em>'.
   * @generated
   */
  StringLiteral createStringLiteral();

  /**
   * Returns a new object of class '<em>Boolean Literal</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Boolean Literal</em>'.
   * @generated
   */
  BooleanLiteral createBooleanLiteral();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  GamlPackage getGamlPackage();

} //GamlFactory
