import api.GradeDataBase;
import entity.Grade;
import entity.Team;
import org.junit.jupiter.api.Test;
import usecase.GetAverageGradeUseCase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetAverageGradeEdgeCasesTest {
    @Test
    void excludesOtherCoursesAndMembersWithNoGradesAndPreservesFraction() {
        GradeDataBase database = mock(GradeDataBase.class);
        when(database.getMyTeam()).thenReturn(new Team("fixture", new String[]{"a", "b", "c"}));
        when(database.getGrades("a")).thenReturn(new Grade[]{
            new Grade("a", "207", 80), new Grade("a", "148", 100)});
        when(database.getGrades("b")).thenReturn(new Grade[]{new Grade("b", "207", 85)});
        when(database.getGrades("c")).thenReturn(new Grade[0]);
        assertEquals(82.5f, new GetAverageGradeUseCase(database).getAverageGrade("207"));
    }

    @Test
    void emptyTeamReturnsZero() {
        GradeDataBase database = mock(GradeDataBase.class);
        when(database.getMyTeam()).thenReturn(new Team("empty", new String[0]));
        assertEquals(0f, new GetAverageGradeUseCase(database).getAverageGrade("207"));
    }

    @Test
    void noMatchingCourseReturnsZero() {
        GradeDataBase database = mock(GradeDataBase.class);
        when(database.getMyTeam()).thenReturn(new Team("fixture", new String[]{"a"}));
        when(database.getGrades("a")).thenReturn(new Grade[]{new Grade("a", "148", 100)});
        assertEquals(0f, new GetAverageGradeUseCase(database).getAverageGrade("207"));
    }
}
