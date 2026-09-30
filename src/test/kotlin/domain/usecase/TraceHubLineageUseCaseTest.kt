package domain.usecase

import com.example.logiroute.com.example.logiroute.domain.model.request.HubNode
import com.example.logiroute.com.example.logiroute.domain.model.request.HubType
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.usecase.TraceHubLineageUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TraceHubLineageUseCaseTest {

    private val useCase = TraceHubLineageUseCase()

    @Test
    fun `returns hub lineage from selected hub to root`() {
        val root = HubNode(
            warehouse = warehouse("WH-001"),
            hubType = HubType.GLOBAL_HUB
        )
        val regional = HubNode(
            warehouse = warehouse("WH-002"),
            hubType = HubType.REGIONAL_CENTER,
            parentHub = root
        )
        val local = HubNode(
            warehouse = warehouse("WH-003"),
            hubType = HubType.LOCAL_DEPOT,
            parentHub = regional
        )

        val result = useCase(local)
        assertEquals(listOf(local, regional, root), result)
    }

    @Test
    fun `root hub lineage contains only root`() {

        val root = HubNode(
            warehouse = warehouse("WH-001"),
            hubType = HubType.GLOBAL_HUB
        )
        val result = useCase(root)
        assertEquals(listOf(root), result)
    }

    private fun warehouse(id: String) = Warehouse(
        id = id,
        name = "Test Warehouse",
        regionalZone = "WEST",
        latitude = 31.5,
        longitude = 34.5
    )
}