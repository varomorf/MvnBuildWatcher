import Logger;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger

import com.jezhumble.javasysmon.JavaSysMon
import com.jezhumble.javasysmon.OsProcess
import com.jezhumble.javasysmon.ProcessVisitor

class UDKSpawner {

	private int uccPid;
	private Logger uccLog;

	/**
	 * Mutex that forces only one child process to be spawned at a time. 
	 * 
	 */
	private static final Object spawnProcessMutex = new Object();

	/**
	 * Spawns a new UDK process and sets {@link #uccPid} to it's PID. To work correctly,
	 * the code relies on the fact that no other method in this JVM runs UDK processes and
	 * that no method kills a process unless it acquires lock on spawnProcessMutex.
	 * @param procBuilder
	 * @return 
	 */
	private Process spawnUDK(ProcessBuilder procBuilder) throws IOException {
		synchronized (spawnProcessMutex){
			JavaSysMon monitor = new JavaSysMon();
			DirectUDKChildProcessVisitor beforeVisitor = new DirectUDKChildProcessVisitor();
			monitor.visitProcessTree(monitor.currentPid(), beforeVisitor);
			Set<Integer> alreadySpawnedProcesses = beforeVisitor.getUdkPids();

			Process proc = procBuilder.start();

			DirectUDKChildProcessVisitor afterVisitor = new DirectUDKChildProcessVisitor();
			monitor.visitProcessTree(monitor.currentPid(), afterVisitor);
			Set<Integer> newProcesses = afterVisitor.getUdkPids();

			newProcesses.removeAll(alreadySpawnedProcesses);

			if(newProcesses.isEmpty()){
				uccLog.severe("There is no new UKD PID.");
			}
			else if(newProcesses.size() > 1){
				uccLog.severe("Multiple new candidate UDK PIDs");
			} else {
				uccPid = newProcesses.iterator().next();
			}
			return proc;
		}
	}

	private void killUDKByPID(){
		if(uccPid < 0){
			uccLog.severe("Cannot kill UCC by PID. PID not set.");
			return;
		}
		synchronized(spawnProcessMutex){
			JavaSysMon monitor = new JavaSysMon();
			monitor.killProcessTree(uccPid, false);
		}
	}

	private static class DirectUDKChildProcessVisitor implements ProcessVisitor {
		Set<Integer> udkPids = new HashSet<Integer>();

		@Override
		public boolean visit(OsProcess op, int i) {
			if(op.processInfo().getName().equals("UDK.exe")){
				udkPids.add(op.processInfo().getPid());
			}
			return false;
		}

		public Set<Integer> getUdkPids() {
			return udkPids;
		}
	}
}

JavaSysMon sysmon = new JavaSysMon()

println(sysmon.currentPid())
Runtime.getRuntime().exec('mvn.bat clean install -DskipTests', null, new File('C:/entorno/repository/openfidelia'))
println 'waiting'
sleep(5000)
def a = {p, i ->
	println p?.processInfo?.pid
	println i
}
sysmon.visitProcessTree(sysmon.currentPid(), a as ProcessVisitor)
sleep(10000)
println 'killing'
sysmon.infanticide()
sleep(5000)