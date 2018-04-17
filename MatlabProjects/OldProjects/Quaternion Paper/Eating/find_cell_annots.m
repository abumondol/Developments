sub_count = length(data);

for ico_number = 4:4
    bite_cells = [];
    drink_cells = [];
    for s = 1:sub_count
        ca = cellseq(ico_number).subject(s).cell_assignment;
        cs = cellseq(ico_number).subject(s).cell_sequence;
        annots = data(s).annots;
        bt = annots(annots(:, 3)<=7, 1);
        dk = annots(annots(:, 3)>=8, 1);
        bite_cells = [bite_cells; ca(bt)];
        drink_cells = [drink_cells; ca(dk)];
    end
        
    unique_bite_cells = unique(bite_cells);
    unique_drink_cells = unique(drink_cells);
    
    ubc_count = length(unique_bite_cells);
    udc_count = length(unique_drink_cells);
    
    bc_freq = zeros(ubc_count, 2);
    dc_freq = zeros(udc_count, 2);
    
    for i = 1:ubc_count
        x = unique_bite_cells(i);        
        bc_freq(i,1) = sum(bite_cells == x);
        bc_freq(i,2) = x;        
    end
    
    bc_freq = sortrows(bc_freq);
    bc_freq = flipud(bc_freq);
    
end
