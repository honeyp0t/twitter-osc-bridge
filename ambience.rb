# Welcome to Sonic Pi v3.1

synth :dull_bell, note: choose(chord(:c5, :major))

live_loop :positive_events do
  use_real_time
  note = sync "/osc/trigger/positive_event"
  synth :dull_bell, note: choose(chord(:c5, :major))
end

live_loop :negative_events do
  use_real_time
  note = sync "/osc/trigger/negative_event"
  synth :zawa, note: :c3
end

live_loop :kick do
  use_synth :fm
  play :d1, amp: 5
  sleep 1
end

#live_loop :ambience do
#  with_fx :reverb, pre_amp: 1, room: 1, mix: 1, damp: 1 do
#    with_fx :reverb, pre_amp: 1.5, room: 1, mix: 1, damp: 1 do
#      use_synth :piano
#      play choose([chord(:c2, :major), chord(:f2, :major), chord(:g2, :major)]),
#        decay: 0.1, attack: 0.2, release: 5
#      sleep 4
#    end
#  end
#end

