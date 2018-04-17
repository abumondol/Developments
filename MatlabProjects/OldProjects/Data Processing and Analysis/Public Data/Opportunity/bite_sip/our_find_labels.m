function res = our_find_labels(window_indices, mid_ix)
    wix = window_indices;
    wcount = size(wix,1);
    labels = zeros(wcount, 1);
    covered = zeros(length(mid_ix), 1);
    
    for i=1:wcount
        s = wix(i, 1);
        e = wix(i, 2);
        ix = find(mid_ix>s & mid_ix<e);
        covered(ix) = covered(ix) + 1;
        labels(i,1)= length(ix);
    end
        
    res.labels2 = labels;
    res.duplicate_count = sum(labels(labels(:,1)>1, :));
    res.max_duplicate_val = max(labels);
    
    labels(labels(:,1)>0, :) = 1;    
    res.labels = labels;
    
    
    
    res.covered2 = covered;
    res.covered_duplicate_count = sum(covered(covered(:,1)>1, :));
    res.covered_max_duplicate_val = max(covered);
    
    covered(covered(:,1)>0, :) = 1;    
    res.covered = covered;    
    res.missed = length(mid_ix) - sum(covered);
    
    res.average_window_length = mean(wix(:,2)-wix(:,1)+1);
    
end