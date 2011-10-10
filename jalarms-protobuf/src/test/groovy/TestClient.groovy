//This is not a unit test, but a script to test a running server
import com.solab.alarms.*

AlarmSender sender = new ProtobufAlarmClient('127.0.0.1', 9998)
sender.sendAlarm('Test 1')
sender.sendAlarm('Test 2', 'source 1')
Thread.sleep(6000)
sender.sendAlarmAlways('Test 1')
sender.sendAlarmAlways('Test 2', 'source 2')
sender.close()
