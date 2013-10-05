package control;

import java.io.IOException;

import org.python.util.PythonInterpreter;

public class JRunner {

	public static void main(String[] args) throws IOException,
			InterruptedException {

		if (args.length < 1) {
			System.out.println("first argument must me module to launch");
			System.exit(1);
		}
		String module = args[0];

		boolean first = true;
		PythonInterpreter p = null;
		while (true) {
			long start = System.currentTimeMillis();

			p = new PythonInterpreter();
			long end = System.currentTimeMillis();
			System.out.printf("jython boot took: %d ms\n", end - start);

			System.out.printf("Press any key to run %s\n", module);
			System.in.read();

			System.out.printf("running %s\n", module);
			try {
				p.exec(String.format("import %s", module));
				p.exec(String.format("reload(%s)", module));
				p.exec(String.format("%s.main()", module));
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Thread.sleep(1000);
		}
	}
}