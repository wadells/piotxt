package persistance.schedule.store;

public class DefaultScheduleTest extends MutableScheduleTestTemplate {

	@Override
	protected MutableSchedule createSchedule() {
		return new DefaultSchedule();
	}

}
