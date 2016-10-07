/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
 
package org.apache.uima.jcas;

import java.io.InputStream;
import java.util.Iterator;
import java.util.ListIterator;

import org.apache.uima.cas.AbstractCas;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.ConstraintFactory;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIndexRepository;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.FSMatchConstraint;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeaturePath;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.FeatureValuePath;
import org.apache.uima.cas.SelectFSs;
import org.apache.uima.cas.SofaFS;
import org.apache.uima.cas.SofaID;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.admin.CASAdminException;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.LowLevelCAS;
import org.apache.uima.cas.impl.LowLevelIndexRepository;
import org.apache.uima.cas.impl.SelectFSs_impl;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.Sofa;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * Java Cover Classes based Object-oriented CAS (Common Analysis System) API.
 * 
 * <p>
 * A <code>JCas</code> object provides the starting point for working with the CAS using
 * Java Cover Classes for each type, generated by the utility JCasGen. 
 * <p>
 * This interface extends the CAS Interface, providing all the same functionality, plus
 * some specific to the JCas.
 * <p>
 * It supports the creation of new instances of CAS types, using the normal Java "new"
 * operator.  
 * <p>
 * You can create a <code>JCas</code> object from a CAS object by calling the getJCas()
 * method on the CAS object. 
 */
public interface JCas extends AbstractCas {

  /**
   * (internal use)
   */
  public final static int INVALID_FEATURE_CODE = -1;

  // *********************************
  // * Getters for read-only objects *
  // *********************************
  /**
   * @return the FSIndexRepository object for this Cas
   */
  FSIndexRepository getFSIndexRepository();

  LowLevelIndexRepository getLowLevelIndexRepository();

  /** 
   * @return the CAS object for this JCas instantiation 
   */
  CAS getCas();

  /* internal use */
  CASImpl getCasImpl();

  /* internal use */
  LowLevelCAS getLowLevelCas();

  /**
   * Given Foo.type, return the corresponding CAS Type object. This is useful in the methods which
   * require a CAS Type, for instance iterator creation.
   * 
   * @param i -
   *          index returned by Foo.type
   * @return the CAS Java Type object for this CAS Type.
   */
  Type getCasType(int i);

  /*
   * Internal use - looks up a type-name-string in the CAS type system and returns the Cas Type
   * object. Throws CASException if the type isn't found
   */
  Type getRequiredType(String s) throws CASException;

  /*
   * Internal use - look up a feature-name-string in the CAS type system and returns the Cas Feature
   * object. Throws CASException if the feature isn't found
   */
  Feature getRequiredFeature(Type t, String s) throws CASException;

  /**
   * @deprecated As of v2.0, use {#getView(String)}. From the view you can access the Sofa data, or
   *             call {@link #getSofa()} if you truly need to access the SofaFS object.
   * @param sofaID -
   * @return the Sofa
   */
  @Deprecated
  Sofa getSofa(SofaID sofaID);

  /**
   * Get the Sofa feature structure associated with this JCas view.
   * 
   * @return The SofaFS associated with this JCas view.
   */  
  Sofa getSofa();

  /**
   * Create a view and its underlying Sofa (subject of analysis). The view provides access to the
   * Sofa data and the index repository that contains metadata (annotations and other feature
   * structures) pertaining to that Sofa.
   * <p>
   * This method creates the underlying Sofa feature structure, but does not set the Sofa data.
   * Setting ths Sofa data must be done by calling {@link #setSofaDataArray(FeatureStructure, String)},
   * {@link #setSofaDataString(String, String)} or {@link #setSofaDataURI(String, String)} on the
   * JCas view returned by this method.
   * 
   * @param sofaID
   *          the local view name, before any sofa name mapping is done, for this view (note: this is the
   *          same as the associated Sofa name).
   * 
   * @return The view corresponding to this local name.
   * @throws CASException -
   *           if a View with this name already exists in this CAS
   */
  JCas createView(String sofaID) throws CASException;

  /**
   * Create a JCas view for a Sofa. 
   * 
   * @param sofa
   *          a Sofa feature structure in this CAS.
   * 
   * @return The JCas view for the given Sofa.
   * @throws CASException -
   */  
  JCas getJCas(Sofa sofa) throws CASException;

  /**
   * Gets the JCas-based interface to the Index Repository. Provides the same functionality
   * as {@link #getFSIndexRepository()} except that the methods that take a "type"
   * argument take type arguments obtainable easily from the JCas type.
   *
   * @return the JCas-based interface to the index repository
   */
  JFSIndexRepository getJFSIndexRepository();

  /**
   * Gets the document annotation. The object returned from this method can be typecast to
   * org.apache.uima.jcas.tcas.DocumentAnnotation
   * <p>
   * The reason that the return type of this method is not DocumentAnnotation is because of problems
   * that arise when using the UIMA Extension ClassLoader to load annotator classes. The
   * DocumentAnnotation type may be defined in the UIMA extension ClassLoader, differently than in
   * the framework ClassLoader.
   * 
   * @return The one instance of the DocumentAnnotation annotation.
   * @see org.apache.uima.cas.CAS#getDocumentAnnotation
   */
  TOP getDocumentAnnotationFs();

  /**
   * A constant for each cas which holds a 0-length instance. Since this can be a common value, we
   * avoid creating multiple copies of it. All uses can use the same valuee because it is not
   * updatable (it has no subfields). This is initialized lazily on first reference, and reset when
   * the CAS is reset.
   * @return 0-length instance of a StringArray
   */

  StringArray getStringArray0L();

  /**
   * A constant for each cas which holds a 0-length instance. Since this can be a common value, we
   * avoid creating multiple copies of it. All uses can use the same valuee because it is not
   * updatable (it has no subfields). This is initialized lazily on first reference, and reset when
   * the CAS is reset.
   * @return 0-length instance of an IntegerArray
   */
  IntegerArray getIntegerArray0L();

  /**
   * A constant for each cas which holds a 0-length instance. Since this can be a common value, we
   * avoid creating multiple copies of it. All uses can use the same valuee because it is not
   * updatable (it has no subfields). This is initialized lazily on first reference, and reset when
   * the CAS is reset.
   * @return 0-length instance of a FloatArray
  FloatArray getFloatArray0L();

  /**
   * A constant for each cas which holds a 0-length instance. Since this can be a common value, we
   * avoid creating multiple copies of it. All uses can use the same valuee because it is not
   * updatable (it has no subfields). This is initialized lazily on first reference, and reset when
   * the CAS is reset.
   * @return 0-length instance of a FSArray
   */
  FSArray getFSArray0L();

  /**
   * initialize the JCas for new Cas content. Not used, does nothing.
   * 
   * @deprecated not required, does nothing
   */
  @Deprecated
  void processInit();

  /**
   * Get the view for a Sofa (subject of analysis). The view provides access to the Sofa data and
   * the index repository that contains metadata (annotations and other feature structures)
   * pertaining to that Sofa.
   * 
   * @param localViewName
   *          the local name, before any sofa name mapping is done, for this view (note: this is the
   *          same as the associated Sofa name).
   * 
   * @return The view corresponding to this local name.
   * @throws CASException passthru
   *           
   */
  JCas getView(String localViewName) throws CASException;
  
  /**
   * Get the view for a Sofa (subject of analysis). The view provides access to the Sofa data and
   * the index repository that contains metadata (annotations and other feature structures)
   * pertaining to that Sofa.
   * 
   * @param aSofa
   *          a Sofa feature structure in the CAS
   * 
   * @return The view for the given Sofa
   * @throws CASException passthru
   */
  JCas getView(SofaFS aSofa) throws CASException;

  /**
   * This part of the CAS interface is shared among CAS and JCAS interfaces
   * If you change it in one of the interfaces, consider changing it in the 
   * other
   */

  
  ///////////////////////////////////////////////////////////////////////////
  //
  //  Standard CAS Methods
  //
  ///////////////////////////////////////////////////////////////////////////
  /**
   * Return the type system of this CAS instance.
   * 
   * @return The type system, or <code>null</code> if none is available.
   * @exception CASRuntimeException
   *              If the type system has not been committed.
   */
  TypeSystem getTypeSystem() throws CASRuntimeException;
  
  /**
   * Create a Subject of Analysis. The new sofaFS is automatically added to the SofaIndex.
   * @param sofaID the SofA ID
   * @param mimeType the mime type
   * @return The sofaFS.
   * 
   * @deprecated As of v2.0, use {@link #createView(String)} instead.
   */
  @Deprecated
  SofaFS createSofa(SofaID sofaID, String mimeType);

  /**
   * Get iterator for all SofaFS in the CAS.
   * 
   * @return an iterator over SofaFS.
   */
  FSIterator<SofaFS> getSofaIterator();

  /**
   * Create an iterator over structures satisfying a given constraint. Constraints are described in
   * the javadocs for {@link ConstraintFactory} and related classes.
   * 
   * @param it
   *          The input iterator.
   * @param cons
   *          The constraint specifying what structures should be returned.
   * @param <T> the particular FeatureStructure type
   * @return An iterator over FSs.
   */
  <T extends FeatureStructure> FSIterator<T> createFilteredIterator(FSIterator<T> it, FSMatchConstraint cons);

  /**
   * Get a constraint factory. A constraint factory is a simple way of creating
   * {@link org.apache.uima.cas.FSMatchConstraint FSMatchConstraints}.
   * 
   * @return A constraint factory to create new FS constraints.
   */
  ConstraintFactory getConstraintFactory();

  /**
   * Create a feature path. This is mainly useful for creating
   * {@link org.apache.uima.cas.FSMatchConstraint FSMatchConstraints}.
   * 
   * @return A new, empty feature path.
   */
  FeaturePath createFeaturePath();

  /**
   * Get the index repository.
   * 
   * @return The index repository, or <code>null</code> if none is available.
   */
  FSIndexRepository getIndexRepository();

  /**
   * Wrap a standard Java {@link java.util.ListIterator ListIterator} around an FSListIterator. Use
   * if you feel more comfortable with java style iterators.
   * 
   * @param it
   *          The <code>FSListIterator</code> to be wrapped.
   * @param <T> The particular Feature Structure type
   * @return An equivalent <code>ListIterator</code>.
   */
  <T extends FeatureStructure> ListIterator<T> fs2listIterator(FSIterator<T> it);

  /**
   * Reset the CAS, emptying it of all content. Feature structures and iterators will no longer be
   * valid. Note: this method may only be called from an application. Calling it from an annotator
   * will trigger a runtime exception.
   * 
   * @throws CASRuntimeException
   *           When called out of sequence.
   * @see org.apache.uima.cas.admin.CASMgr
   */
  void reset() throws CASAdminException;
  
  /**
   * Get the view name. The view name is the same as the name of the view's Sofa, retrieved by
   * getSofa().getSofaID(), except for the initial View before its Sofa has been created.
   * 
   * @return The name of the view
   */
  String getViewName();

  /**
   * Estimate the memory consumption of this CAS instance (in bytes).
   * 
   * @return The estimated memory used by this CAS instance.
   */
  int size();

  /**
   * Create a feature-value path from a string.
   * 
   * @param featureValuePath
   *          String representation of the feature-value path.
   * @return Feature-value path object.
   * @throws CASRuntimeException
   *           If the input string is not well-formed.
   */
  FeatureValuePath createFeatureValuePath(String featureValuePath) throws CASRuntimeException;

  /**
   * Set the document text. Once set, Sofa data is immutable, and cannot be set again until the CAS
   * has been reset.
   * 
   * @param text
   *          The text to be analyzed.
   * @exception CASRuntimeException
   *              If the Sofa data has already been set.
   */
  void setDocumentText(String text) throws CASRuntimeException;

  /**
   * Set the document text. Once set, Sofa data is immutable, and cannot be set again until the CAS
   * has been reset.
   * 
   * @param text
   *          The text to be analyzed.
   * @param mimetype
   *          The mime type of the data
   * @exception CASRuntimeException
   *              If the Sofa data has already been set.
   */
  void setSofaDataString(String text, String mimetype) throws CASRuntimeException;

  /**
   * Get the document text.
   * 
   * @return The text being analyzed.
   */
  String getDocumentText();

  /**
   * Get the Sofa Data String (a.k.a. the document text).
   * 
   * @return The Sofa data string.
   */
  String getSofaDataString();

  /**
   * Sets the language for this document. This value sets the language feature of the special
   * instance of DocumentAnnotation associated with this CAS.
   * 
   * @param languageCode the language code
   * @throws CASRuntimeException passthru
   */
  void setDocumentLanguage(String languageCode) throws CASRuntimeException;

  /**
   * Gets the language code for this document from the language feature of the special instance of
   * the DocumentationAnnotation associated with this CAS.
   * 
   * @return language identifier
   */
  String getDocumentLanguage();

  /**
   * Set the Sofa data as an ArrayFS. Once set, Sofa data is immutable, and cannot be set again
   * until the CAS has been reset.
   * 
   * @param array
   *          The ArrayFS to be analyzed.
   * @param mime
   *          The mime type of the data
   * @exception CASRuntimeException
   *              If the Sofa data has already been set.
   */
  void setSofaDataArray(FeatureStructure array, String mime) throws CASRuntimeException;

  /**
   * Get the Sofa data array.
   * 
   * @return The Sofa Data being analyzed.
   */
  FeatureStructure getSofaDataArray();

  /**
   * Set the Sofa data as a URI. Once set, Sofa data is immutable, and cannot be set again until the
   * CAS has been reset.
   * 
   * @param uri
   *          The URI of the data to be analyzed.
   * @param mime
   *          The mime type of the data
   * @exception CASRuntimeException
   *              If the Sofa data has already been set.
   */
  void setSofaDataURI(String uri, String mime) throws CASRuntimeException;

  /**
   * Get the Sofa data array.
   * 
   * @return The Sofa Data being analyzed.
   */
  String getSofaDataURI();

  /**
   * Get the Sofa data as a byte stream.
   * 
   * @return A stream handle to the Sofa Data.
   */
  InputStream getSofaDataStream();

  /**
   * Get the mime type of the Sofa data being analyzed.
   * 
   * @return the mime type of the Sofa
   */
  String getSofaMimeType();
  
  /**
   * Add a feature structure to all appropriate indexes in the repository associated with this CAS
   * View.
   * 
   * <p>
   * <b>Important</b>: after you have called <code>addFsToIndexes(...)</code> on a FS, do not
   * change the values of any features used for indexing. If you do, the index will become corrupted
   * and may be unusable. If you need to change an index feature value, first call
   * {@link #removeFsFromIndexes(FeatureStructure) removeFsFromIndexes(...)} on the FS, change the
   * feature values, then call <code>addFsToIndexes(...)</code> again.
   * 
   * @param fs
   *          The Feature Structure to be added.
   * @exception NullPointerException
   *              If the <code>fs</code> parameter is <code>null</code>.
   */
  void addFsToIndexes(FeatureStructure fs);

  /**
   * Remove a feature structure from all indexes in the repository associated with this CAS View.
   * 
   * @param fs
   *          The Feature Structure to be removed.
   * @exception NullPointerException
   *              If the <code>fs</code> parameter is <code>null</code>.
   */
  void removeFsFromIndexes(FeatureStructure fs);
  
  /**
   * Remove all feature structures of a given type (including subtypes) from all indexes in the repository associated with this CAS View.
   * @param i the CAS type constant, written as Foo.type (for a given JCas Type) or anInstanceOfFoo.getTypeIndexID(), for an instance
   */
  void removeAllIncludingSubtypes(int i);
  
  /**
   * Remove all feature structures of a given type (excluding subtypes) from all indexes in the repository associated with this CAS View.
   * @param i the CAS type constant, written as Foo.type (for a given JCas Type) or anInstanceOfFoo.getTypeIndexID(), for an instance
   */
  void removeAllExcludingSubtypes(int i);

  /**
   * Get the standard annotation index.
   * 
   * @return The standard annotation index.
   */
  AnnotationIndex<Annotation> getAnnotationIndex();

  /**
   * Get the standard annotation index restricted to a specific annotation type.
   * 
   * @param type
   *          The annotation type the index is restricted to.
   * @param <T> the Java class corresponding to type
   * @return The standard annotation index, restricted to <code>type</code>.
   */
  <T extends Annotation> AnnotationIndex<T> getAnnotationIndex(Type type) throws CASRuntimeException;

  /**
   * Get the standard annotation index restricted to a specific annotation type.
   * 
   * @param type
   *          The annotation type the index is restricted to, 
   *          passed as an integer using the form
   *          MyAnnotationType.type
   * @param <T> the Java class corresponding to type
   * @return The standard annotation index, restricted to <code>type</code>.
   * @throws CASRuntimeException -
   */
  <T extends Annotation> AnnotationIndex<T> getAnnotationIndex(int type) throws CASRuntimeException;

  /**
   * Get the standard annotation index restricted to a specific annotation type.
   * 
   * @param clazz The JCas cover class for the annotation type the index is restricted to, 
   * @param <T> the Java class clazz
   * @return The standard annotation index, restricted to <code>type</code>.
   * @throws CASRuntimeException -
   */
  <T extends Annotation> AnnotationIndex<T> getAnnotationIndex(Class<T> clazz) throws CASRuntimeException;

  /**
   * Gets an iterator over all indexed FeatureStructures of the specified Type (and any of its
   * subtypes).  The elements are returned in arbitrary order, and duplicates (if they exist)
   * are not removed.
   *
   * @param clazz - the JCas Java class specifing which type and subtypes are included
   * @param <T> the Java clazz
   *  
   * @return An iterator that returns all indexed FeatureStructures of the JCas clazz 
   *         and its subtypes, in no particular order.
   */
  <T extends TOP> FSIterator<T> getAllIndexedFS(Class<T> clazz);

  
  /**
   * Get iterator over all views in this JCas.  Each view provides access to Sofa data
   * and the index repository that contains metadata (annotations and other feature 
   * structures) pertaining to that Sofa.
   * 
   * @return an iterator which returns all views.  Each object returned by
   *   the iterator is of type JCas.
   * @throws CASException -
  */
  Iterator<JCas> getViewIterator() throws CASException;  
  
  /**
   * Get iterator over all views with the given name prefix.  Each view provides access to Sofa data
   * and the index repository that contains metadata (annotations and other feature 
   * structures) pertaining to that Sofa.
   * <p>
   * When passed the prefix <i>namePrefix</i>, the iterator will return all views who 
   * name is either exactly equal to <i>namePrefix</i> or is of the form 
   * <i>namePrefix</i><code>.</code><i>suffix</i>, where <i>suffix</i> can be any String.
   * 
   * @param localViewNamePrefix  the local name prefix, before any sofa name mapping 
   *   is done, for this view (note: this is the same as the associated Sofa name prefix).
   * 
   * @return an iterator which returns all views with the given name prefix.  
   *   Each object returned by the iterator is of type JCas.
   * @throws CASException -
   */
  Iterator<JCas> getViewIterator(String localViewNamePrefix) throws CASException;
  
  /**
   * Call this method to set up a region, 
   * ended by a close() call on the returned object,
   * You can use this or the {@link #protectIndexes(Runnable)} method to protected
   * the indexes.
   * <p>
   * This approach allows arbitrary code between  the protectIndexes and the associated close method.
   * <p>
   * The close method is best done in a finally block, or using the try-with-resources statement in 
   * Java 8.
   * 
   * @return an object used to record things that need adding back
   */
  AutoCloseable protectIndexes();
  
  /**
   * Runs the code in the runnable inside a protection block, where any modifications to features
   * done while in this block will be done in a way to protect any indexes which otherwise 
   * might become corrupted by the update action; the protection is achieved by temporarily
   * removing the FS (if it is in the indexes), before the update happens.
   * At the end of the block, affected indexes have any removed-under-the-covers FSs added back.
   * @param runnable code to execute while protecting the indexes. 
   */
  void protectIndexes(Runnable runnable);
  
  /**
   * Retrieve an index according to a label and a type specified using a JCas class. 
   * The type is used to narrow down the index of a more general type to a more specific one.
   * 
   * Generics: T is the associated Java cover class for the type.
   * 
   * @param label The name of the index.
   * @param clazz The JCas class (mostly likely written as MyJCasClass.class), which must correspond to a subtype of the type of the index.
   * @param <T> the Java clazz
   * @return The specified, or <code>null</code> if an index with that name doesn't exist.
   * @exception CASRuntimeException When <code>clazz</code> doesn't correspond to a subtype of the index's type.
   */
  <T extends TOP> FSIndex<T> getIndex(String label, Class<T> clazz);
  
  
  default <T extends FeatureStructure> SelectFSs<T> select() {
    return new SelectFSs_impl<>(getCas());
  }

  default <N extends FeatureStructure> SelectFSs<N> select(Type type) {
    return new SelectFSs_impl<>(getCasImpl()).type(type);
  }

  default <N extends FeatureStructure> SelectFSs<N> select(Class<N> clazz) {
    return new SelectFSs_impl<>(getCasImpl()).type(clazz);
  }

  default <N extends FeatureStructure> SelectFSs<N> select(int jcasType) {
    return new SelectFSs_impl<>(getCasImpl()).type(jcasType);
  }

  default <N extends FeatureStructure> SelectFSs<N> select(String fullyQualifiedTypeName) {
    return new SelectFSs_impl<>(getCasImpl()).type(fullyQualifiedTypeName);
  }

}