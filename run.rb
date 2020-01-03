def mtime; File.stat('show.cljs').mtime end
def lumo; Process.spawn 'lumo show.cljs' end

last_modified = mtime
last_lumo = lumo

puts 'waiting...'

loop do
  sleep 1
  next if last_modified == mtime

  puts 'reloading'
  last_modified = mtime
  Process.kill(:SIGINT, last_lumo)
  last_lumo = lumo
end
