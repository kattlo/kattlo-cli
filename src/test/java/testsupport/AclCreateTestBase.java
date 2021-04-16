package testsupport;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateAclsResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.acl.AclBinding;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

public abstract class AclCreateTestBase {

    @Mock
    protected AdminClient admin;

    @Mock
    CreateAclsResult result;

    @Mock
    KafkaFuture<Void> future;

    @Captor
    protected ArgumentCaptor<Collection<AclBinding>> newACLCaptor;

    protected void mockitoWhen() throws Exception {

        when(admin.createAcls(anyCollection()))
            .thenReturn(result);

        when(result.all())
            .thenReturn(future);

        when(future.get())
            .thenReturn((Void)null);

    }
}
