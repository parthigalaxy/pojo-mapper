package com.pojo.mapper.annotation.process;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.pojo.mapper.annotation.types.Mapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

//import lombok.extern.slf4j.Slf4j;

@AutoService(Processor.class)
//@Slf4j
public class MapperProcessor extends AbstractProcessor{

	private static final String DOT = ".";
	private Types typeUtils;
	private Elements elementUtils;
	private Filer filer;
	private Messager messager;
	
	private static final String SUFFIX = "Impl";
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.typeUtils = processingEnv.getTypeUtils();
		this.elementUtils = processingEnv.getElementUtils();
		this.filer = processingEnv.getFiler();
		this.messager = processingEnv.getMessager();
	}
	
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> annotations = new LinkedHashSet<String>();
		annotations.add(Mapper.class.getCanonicalName());
		return annotations;
	}
	
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		
		for(Element annotatedElement : roundEnv.getElementsAnnotatedWith(Mapper.class)) {
				System.out.println("element Kind -> " + annotatedElement.getKind());
				if(annotatedElement.getKind() != ElementKind.METHOD) {
					passError(annotatedElement, "Only Method can annotated with @%s", Mapper.class.getSimpleName());
					return true;
				}
				
				PackageElement packageElement = elementUtils.getPackageOf(annotatedElement);
				Element nameElement = annotatedElement.getEnclosingElement();
				String className =  annotatedElement.getEnclosingElement().getSimpleName().toString()+SUFFIX;
				
				String methodName = annotatedElement.getSimpleName().toString();
				System.out.println("Element Simple Name ->"+ annotatedElement.getSimpleName());
				
				System.out.println("Package Name -> "+packageElement.getSimpleName());
				System.out.println("Qualified Name -> "+packageElement.getQualifiedName());
				
				System.out.println("Class -> "+className);
				
				String qualifiedClassName = packageElement.getQualifiedName().toString();
				
				
				Set<Modifier> modifiers = annotatedElement.getModifiers();
				modifiers.forEach(System.out::println);
				Set<Modifier> methodModifiers = modifiers.stream().filter(modifier -> !Modifier.ABSTRACT.equals(modifier)).collect(Collectors.toSet());

				List<? extends Element>  enclosedElements = annotatedElement.getEnclosedElements();
				for (Element enclosed : enclosedElements ) {
					System.out.println("***************************************");
					System.out.println("Kind : "+enclosed.getKind());
					System.out.println("Name : "+enclosed.getSimpleName());
					System.out.println("***************************************");
				}
				
				TypeMirror typeMirror = annotatedElement.asType();
				
				Set<String> destinationMethods = new HashSet<>();
				Set<String> sourceMethods = new HashSet<>();
				
				if(TypeKind.EXECUTABLE.equals(typeMirror.getKind())) {
					ExecutableElement executableElement = (ExecutableElement)annotatedElement;
					TypeMirror returnType = executableElement.getReturnType();
					System.out.println("returnType -> "+ returnType.toString());
					System.out.println("returnType Kind -> "+ returnType.getKind());
					if(TypeKind.DECLARED.equals(returnType.getKind())) {
						 DeclaredType declaredType = (DeclaredType) returnType;
						 System.out.println("returnType declaredType -> "+ declaredType.asElement().getEnclosedElements());
						 System.out.println("***************************************");
						 for(Element element: declaredType.asElement().getEnclosedElements()) {
							 System.out.println("Kind : "+element.getKind());
							 System.out.println("SimpleName : "+element.getSimpleName());
							 if(ElementKind.METHOD.equals(element.getKind())
									 && element.getSimpleName().toString().startsWith("set")) {
								 destinationMethods.add(element.getSimpleName().toString());
							 }
						 }
						 System.out.println("***************************************");
					}
					
					List<? extends VariableElement> variableElements = executableElement.getParameters();
					for(VariableElement variableElement : variableElements) {
						System.out.println("variableElement -> "+ variableElement.toString());
						System.out.println("variableElement Type -> "+ variableElement.asType());
						System.out.println("variableElement Type Kind-> "+ variableElement.asType().getKind());
						if(TypeKind.DECLARED.equals(variableElement.asType().getKind())) {
							 DeclaredType declaredType = (DeclaredType) variableElement.asType();
							 System.out.println("declaredType -> "+ declaredType.asElement().getEnclosedElements());
							 System.out.println("***************************************");
							 for(Element element: declaredType.asElement().getEnclosedElements()) {
								 System.out.println("Kind : "+element.getKind());
								 System.out.println("SimpleName : "+element.getSimpleName());
								 if(ElementKind.METHOD.equals(element.getKind())
										 && element.getSimpleName().toString().startsWith("get")) {
									 sourceMethods.add(element.getSimpleName().toString());
								 }
							 }
							 System.out.println("***************************************");
						}
					}
					
				}
				
				System.out.println("**************** destinationMethods ***********************");
				destinationMethods.forEach(System.out::println);
				System.out.println("***************************************");
				
				System.out.println("**************** sourceMethods ***********************");
				sourceMethods.forEach(System.out::println);
				System.out.println("***************************************");
				
				System.out.println("***************************************");
				System.out.println("Kind : "+typeMirror.getKind());
				System.out.println("TypeMirror : "+typeMirror.toString());
				System.out.println("***************************************");
				
				try {
					JavaFileObject jfo = filer.createSourceFile(qualifiedClassName+DOT+className);
					Writer writer = jfo.openWriter();
					
					Builder mapperBuilder = MethodSpec.methodBuilder(methodName)
						    .addModifiers(methodModifiers);
						    
					if(TypeKind.EXECUTABLE.equals(typeMirror.getKind())) {
						ExecutableElement executableElement = (ExecutableElement)annotatedElement;
						TypeMirror returnType = executableElement.getReturnType();
						mapperBuilder.returns(TypeName.get(returnType));
						
						ClassName resultVar = (ClassName) ClassName.get(returnType);
						
						List<? extends VariableElement> variableElements = executableElement.getParameters();
						List<String> paramName = new ArrayList<>();
						for(VariableElement variableElement : variableElements) {
							paramName.add(variableElement.toString());
							mapperBuilder.addParameter(TypeName.get(variableElement.asType()), variableElement.toString());
						}
						
						System.out.println("canonicalName : "+resultVar.canonicalName());
						System.out.println("simpleName : "+resultVar.simpleName());
						System.out.println("reflectionName : "+resultVar.reflectionName());
						System.out.println("simpleName : "+CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, resultVar.simpleName()));
						
						String returnVarName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, resultVar.simpleName());
						
						mapperBuilder = mapperBuilder.addStatement("$T.out.println($S)", System.class, "Car Model Mapper Impl Start!");

						mapperBuilder.addStatement("$T $L = new $T()", resultVar,returnVarName, resultVar);
						
						for (String destinationMethod : destinationMethods) {
							String sourceMethodName = sourceMethods.stream().filter(sourceMethodNameF -> sourceMethodNameF.substring(2).equals(destinationMethod.substring(2))).findFirst().get();
							mapperBuilder.addStatement("$L.$L($L.$L())", returnVarName,destinationMethod,paramName.get(0),sourceMethodName);
						}
						
						MethodSpec mapperMethodSpec = mapperBuilder.addStatement("$T.out.println($S)", System.class, "Car Model Mapper Impl Emd!")
								.addStatement("return $L", returnVarName)
								.build();
						
						TypeSpec implClassTypeSpec = TypeSpec.classBuilder(className)
								.addSuperinterface(nameElement.asType())
								.addModifiers(Modifier.PUBLIC)
								.addMethod(mapperMethodSpec)
								.build();
						JavaFile javaFile = JavaFile.builder(qualifiedClassName, implClassTypeSpec).build();
						
						System.out.println(javaFile.toString());
						javaFile.writeTo(writer);
					}
					
					writer.close();
					System.out.println("MapperProcessor Successfull.");
						
				} catch (IOException e) {
					e.printStackTrace();
				}
				
		}
		
		return false;
	}

	private void passError(Element annotatedElement, String message, String simpleName) {
		this.messager.printMessage(Kind.ERROR, String.format(message, simpleName), annotatedElement);
	}

}
