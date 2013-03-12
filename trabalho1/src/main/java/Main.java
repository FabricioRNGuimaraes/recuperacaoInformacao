import br.ufla.ri.util.MountList;

public class Main {
	

	public static void main(String[] args) {

		MountList mountList = new MountList();
		mountList.createDocumentsDesktop();
		mountList.calculateRecallPrecision();
		
	}
}
