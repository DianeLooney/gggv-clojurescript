require 'open3'

def mtime;
  [
    File.stat('show.cljs').mtime,
    File.stat('runtime.cljs').mtime,
  ].max
end
def spawn_lumo
  _, stdout, stderr, thread = Open3.popen3('lumo show.cljs')
  puts stdout.gets
  thr = thread[:pid]
  puts thr
  thr
end

last_modified = mtime
last_lumo = spawn_lumo

puts 'waiting...'

loop do
  sleep 0.05
  next if last_modified == mtime

  puts 'reloading'
  last_modified = mtime
  next_lumo = spawn_lumo
  Process.kill(:SIGINT, last_lumo)
  last_lumo=next_lumo
end