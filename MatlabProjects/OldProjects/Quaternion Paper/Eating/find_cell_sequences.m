sub_count = length(data);

cellseq = [];
for s = 1:sub_count
    d = data(s).data;
    for ico_number = 3:4
        [cell_assignment, cell_sequence] = spmo_cell_assignment_and_sequence(d(:,1), d(:,2:4), ico, ico_number);
        cellseq(ico_number).subject(s).cell_assignment = cell_assignment;
        cellseq(ico_number).subject(s).cell_sequence = cell_sequence; 
    end
end
save('cellseq', 'cellseq');