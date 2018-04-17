angle = 10;
ico_number = 4;
load_data;

potential_bites = [];
for s = 1:12
    fprintf('Subject %d\n', s);
    d = data(s).data(:, 2);
    ca = cellseq(4).subject(s).cell_assignment;
    cs = cellseq(4).subject(s).cell_sequence;
    cs_count = length(cs);
    
    minx = zeros(cs_count, 2);
    for i= 1:cs_count
        a = cs(i, 2);
        b = cs(i, 3);
        [M, I] = min(d(a:b));
        minx(i, :) = [M, a+I-1];
    end
    
    indices = [];    
    for i= 2:cs_count-1
        if minx(i, 1)<= minx(i-1,1) && minx(i, 1)<minx(i+1, 1)
            indices = [indices; minx(i, 2)];
        end        
    end
        
    potential_bites(s).indices = indices;
    
    annots = data(s).annots;
    bites = annots(annots(:, 3)<=7, :);
    bite_count = length(bites);
    
    labels = zeros(length(indices), 1);
    for i=1:bite_count
        dst = abs(indices - bites(i,1));
        [M, I] = min(dst);
        labels(I) = 1;
    end
    
    potential_bites(s).labels = labels;
    fprintf('Subject %d: %d, %d\n', s, bite_count, sum(labels));
    
end

save('potential_bites', 'potential_bites');