package hudson.plugins.valgrind;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import hudson.plugins.analysis.core.AbstractHealthDescriptor;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.NullHealthDescriptor;
import hudson.plugins.analysis.test.AbstractHealthDescriptorTest;
import hudson.plugins.analysis.util.model.AnnotationProvider;

import org.junit.Test;
import org.jvnet.localizer.Localizable;

/**
 * Tests the class {@link LeaksHealthDescriptor}.
 *
 * @author Ulli Hafner
 */
public class LeaksHealthDescriptorTest extends AbstractHealthDescriptorTest {
    /**
     * Verifies the different messages if the number of items are 0, 1, and 2.
     */
    @Test
    public void verifyNumberOfItems() {
        AnnotationProvider provider = mock(AnnotationProvider.class);
        LeaksHealthDescriptor healthDescriptor = new LeaksHealthDescriptor(NullHealthDescriptor.NULL_HEALTH_DESCRIPTOR);

        Localizable description = healthDescriptor.createDescription(provider);
        assertEquals(WRONG_DESCRIPTION, Messages.Valgrind_ResultAction_HealthReportNoItem(), description.toString());

        when(provider.getNumberOfAnnotations()).thenReturn(1);
        description = healthDescriptor.createDescription(provider);
        assertEquals(WRONG_DESCRIPTION, Messages.Valgrind_ResultAction_HealthReportSingleItem(), description.toString());

        when(provider.getNumberOfAnnotations()).thenReturn(2);
        description = healthDescriptor.createDescription(provider);
        assertEquals(WRONG_DESCRIPTION, Messages.Valgrind_ResultAction_HealthReportMultipleItem(2), description.toString());
    }

    /** {@inheritDoc} */
    @Override
    protected AbstractHealthDescriptor createHealthDescriptor(final HealthDescriptor healthDescriptor) {
        return new LeaksHealthDescriptor(healthDescriptor);
    }
}
