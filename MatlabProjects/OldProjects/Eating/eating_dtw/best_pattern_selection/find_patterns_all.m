patterns = [];
for sid=1:1%22
    patterns(sid).pats = find_patterns_for_subject(sid, distances);    
end
return

for sid=37:52
    patterns(sid).pats = find_patterns_for_subject(sid, distances);    
end

save('patterns', 'patterns');
