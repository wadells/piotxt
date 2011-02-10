package persistance.schedule.store;

import persistance.schedule.Stop;

/** A generic predicate that can be applied to a stop. Package-internal. */
interface StopPredicate {
	
	public boolean applies(Stop s);
	
}
