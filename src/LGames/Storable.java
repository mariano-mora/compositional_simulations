package LGames;

import java.io.IOException;

import Util.DataCheckException;

public interface Storable {
	
	public void store(String dirName) throws DataCheckException, IOException;
	public void storeAsCSV(String dirName, int interaction) throws DataCheckException, IOException, GrammarException;
}
