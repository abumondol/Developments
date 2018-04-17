angle = 10;
ico_number = 4;
load_data;

potential_bites = [];
for s = 1:12
    fprintf('Subject %d\n', s);
    d = data(s).data(:, 2);
    ca = cellseq(4).subject(s).cell_assignment;
    cs = cellseq(4).subject(s).cell_sequence;
    sample_count = length(d);
    seg_count = length(25:50:sample_count-50);
    
    minx = zeros(seg_count, 2);
    for i= 1:seg_count
        ix = (i-1)*50+1;        
        [M, I] = min(d(ix:ix+49));
        minx(i, :) = [M, ix+I-1];
    end
    
    indices = [];    
    for i= 2:seg_count-1
        if minx(i, 1)<= minx(i-1,1) && minx(i, 1)<minx(i+1, 1) && minx(i, 1)<0
            indices = [indices; minx(i, 2)];
        end        
    end
        
    potential_bites(s).indices = indices;
    
    annots = data(s).annots;    
    annot_count = length(annots);
    
    labels = zeros(length(indices), 1);
    for i=1:annot_count
        ix = annots(i,1);
        pos = find(indices>=ix-100 & indices<=ix+100);
        if ~isempty(pos)
            labels(pos) = 1;
        end
    end
    
    potential_bites(s).labels = labels;
    fprintf('Subject %d: %d, %d\n', s, annot_count, sum(labels));
    
end

save('potential_bites', 'potential_bites');