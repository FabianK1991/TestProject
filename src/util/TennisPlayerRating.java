package util;

public class TennisPlayerRating {
	public double CustomRanking;			// Ranking calulated with time influence (more recent games count more)
	public double CustomRankingAllTime;		// Ranking without time influence
	
	public double CurrentRanking;			// Current Ranking from ATP
	
	public double recentPerformance;		// How good was the performance of the player in the last 6 months
	public double playingActivity;			// How frequent was the player playing in the last 6 months
	
	public double unexpectedWinFrequency;	// Frequency of unexpected wins 	(0.0-1.0)
	public double unexpectedLossFrequency;	// Frequency of unexpected losses 	(0.0-1.0)
}
