package persistance.schedule.store;

public class DefaultScheduleTest extends MutableScheduleTest {

	@Override
	protected MutableSchedule createSchedule() {
		return new DefaultSchedule();
	}

}
