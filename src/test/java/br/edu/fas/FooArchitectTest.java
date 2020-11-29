package br.edu.fas;

import br.edu.fas.persistence.Dao;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class FooArchitectTest {

    JavaClasses importedClasses = new ClassFileImporter().importPackages("br.edu.fas");

    @Test
    public void verificarDepencenciasParaCamadaPersistencia() {

        ArchRule rule = classes()
                .that().resideInAPackage("..persistence..")
                .should().onlyHaveDependentClassesThat().resideInAnyPackage("..persistence..", "..service");

        rule.check(importedClasses);

    }

    @Test
    public void verificarDepencenciasDaCamadaPersistencia() {

        ArchRule rule = noClasses()
                .that().resideInAPackage("..persistence")
                .should().onlyDependOnClassesThat().resideInAnyPackage("..service");

        rule.check(importedClasses);

    }

    @Test
    public void verificarNomesClaseesCamadaPersistencia() {

        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Dao")
                .should().resideInAPackage("..persistence");

        rule.check(importedClasses);

    }

    @Test
    public void verificarImplementacaoInterfaceDao() {

        ArchRule rule = classes()
                .that().implement(Dao.class)
                .should().haveSimpleNameEndingWith("Dao");

        rule.check(importedClasses);

    }

    @Test
    public void verificarDependenciasCiclicas() {

        ArchRule rule = slices()
                .matching("br.edu.fas.(*)..").should().beFreeOfCycles();
        rule.check(importedClasses);
    }

    @Test
    public void verificarViolacaideCamadas() {

        ArchRule rule = layeredArchitecture()
                .layer("Service").definedBy("..service")
                .layer("Persistence").definedBy("..persistence")
                .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service");
        rule.check(importedClasses);

    }
}
