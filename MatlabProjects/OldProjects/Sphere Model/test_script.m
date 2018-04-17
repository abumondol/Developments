data = realdisp_quat;
sess = 1;
counts = zeros(36, 17);
for sub = 1:17    
    labels = data(sub).session(sess).labels;    
    for act = 1:33
        counts(act, sub) = sum(labels==act);        
    end
    
    counts(34, sub) = sum(labels == 0);
    counts(35, sub) = sum(counts(1:34,sub));
    counts(36, sub) = length(labels);    
end