function print_cell_sequence(sequence)
    s = sequence;
    len = size(sequence,1);    
    for i = 1:len
        fprintf('%d, %d, %d\n', s(i,1), s(i,6), s(i,7));
    end    
end