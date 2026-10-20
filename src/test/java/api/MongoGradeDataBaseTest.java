package api;

import entity.Team;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MongoGradeDataBaseTest {
    private MongoGradeDataBase database(String payload) throws IOException {
        return database(payload, 200);
    }
    private MongoGradeDataBase database(String payload, int status) throws IOException {
        OkHttpClient client = mock(OkHttpClient.class);
        Call call = mock(Call.class);
        when(client.newCall(any(Request.class))).thenReturn(call);
        Response response = new Response.Builder()
            .request(new Request.Builder().url("https://example.invalid/team").build())
            .protocol(Protocol.HTTP_1_1).code(status).message("fixture")
            .body(ResponseBody.create(MediaType.parse("application/json"), payload)).build();
        when(call.execute()).thenReturn(response);
        return new MongoGradeDataBase(client, "synthetic-test-token");
    }
    @Test void parsesTeamAndMembers() throws Exception {
        Team team = database("{\"status_code\":200,\"team\":{\"name\":\"test\",\"members\":[\"a\",\"b\"]}}").getMyTeam();
        assertEquals("test", team.getName());
        assertArrayEquals(new String[]{"a", "b"}, team.getMembers());
    }
    @Test void apiErrorIsNotReportedAsAnEmptySuccessfulTeam() throws Exception {
        RuntimeException error = assertThrows(RuntimeException.class,
            () -> database("{\"status_code\":400,\"message\":\"Not in a team\"}").getMyTeam());
        assertEquals("Not in a team", error.getMessage());
    }
    @Test void malformedResponseFailsClearly() throws Exception {
        assertThrows(RuntimeException.class, () -> database("not json").getMyTeam());
    }
    @Test void httpFailureIsNotAcceptedAsASuccessfulTeam() throws Exception {
        RuntimeException error = assertThrows(RuntimeException.class,
            () -> database("{\"status_code\":200,\"team\":{\"name\":\"test\",\"members\":[]}}", 503).getMyTeam());
        assertEquals("Team request failed with HTTP 503", error.getMessage());
    }
    @Test void requestUsesExpectedEndpointAndHeaderAndPropagatesIOFailure() throws Exception {
        OkHttpClient client = mock(OkHttpClient.class);
        Call call = mock(Call.class);
        when(client.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenThrow(new IOException("offline fixture"));
        assertThrows(RuntimeException.class, () -> new MongoGradeDataBase(client, "synthetic-test-token").getMyTeam());
        ArgumentCaptor<Request> request = ArgumentCaptor.forClass(Request.class);
        verify(client).newCall(request.capture());
        assertEquals("/team", request.getValue().url().encodedPath());
        assertEquals("GET", request.getValue().method());
        assertEquals("synthetic-test-token", request.getValue().header("token"));
    }
}
