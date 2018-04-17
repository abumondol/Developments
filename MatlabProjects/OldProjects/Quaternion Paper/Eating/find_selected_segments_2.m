selected_segments = [];

for s = 1:12
    train_data = [];
    test_data = [];
    bite_cell_freq = bite_cell_stat(s).freq;    
    
    for i = 1:12        
        indices = potential_bites(i).indices;
        labels = potential_bites(i).labels;
        ca = cellseq(4).subject(i).cell_assignment;
        
        selected = [];
        indices_count = length(indices);
        for j =1:indices_count
            ix = indices(j);
            cell_no = ca(ix);
            
            if bite_cell_freq(cell_no, 2)>0
                selected = [selected; ix, labels(j)];
            end
        end
        fprintf('Subjects %d, %d: %d, %d; %d, %d\n',s, i, indices_count, length(selected), sum(labels), sum(selected(:,2)) );
        selected_segments(s).subject(i).segments = selected;
    end
    
end

save('selected_segments','selected_segments');