bite_res = zeros(52, 4);
for i=1:1 %22
    sr = get_subject_data_range(i);
    pats = patterns(i).pats;    
    for sid = sr(1):sr(end)        
        [TP, TN, FP, FN]= detect_subject_bites(sid, distances, pats, segments_for_pat);
        bite_res(sid, :) = [TP, TN, FP, FN];        
    end
end

return


for sid=37:52
    pats = patterns(sid).pats;    
    labels = segments(sid).labels(:,1);
    [TP, TN, FP, FN] = detect_subject_bites(sid, labels, distances, pats, segments_for_pat);
    bite_res(sid, :) = [TP, TN, FP, FN];
end

save('bite_res', 'bite_res');
