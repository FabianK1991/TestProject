package testing;

public abstract class ModelWrapper {
	// inits the model and does parsing and learning and stuff
	public abstract void init(String player, String tillTime);
	
	// calculates the prediction of the given gameId of the player
	public abstract double predictGame(String gameId);
}
