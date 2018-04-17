sub_count = length(data);
ico_number = 4;
total_cell_count = length(ico(ico_number).vertices)
neighbors = ico(ico_number).neighbors;

bite_cell_stat = [];
for i = 1:12
    bc = [];
    for s = 1:12
        if i == s
            continue
        end
        ca = cellseq(ico_number).subject(s).cell_assignment;
        %ix = data(s).annots(:,1);        
        
        pbix = potential_bites(s).indices;
        pbl = potential_bites(s).labels;
        ix = pbix(pbl==1);
        
        bc = [bc; ca(ix)];
    end
    
    cell_freq = zeros(total_cell_count, 2);     
    bc_count = length(bc);
    
    for j = 1:bc_count
        x = bc(j);
        cell_freq(x, 1) = cell_freq(x, 1) + 1;        
        cell_freq(x, 2) = 1;
        
        nb = neighbors{x};
        cell_freq(nb, 2) = 1;        
    end
    
    bite_cell_stat(i).freq = cell_freq;
    sum(cell_freq)
end

save('bite_cell_stat','bite_cell_stat');
