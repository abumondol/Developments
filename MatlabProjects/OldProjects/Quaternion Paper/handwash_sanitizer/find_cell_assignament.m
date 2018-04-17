
cellassign = [];
for s=1:10
    eat_data = data(s).eat_data;
    hw_data = data(s).hw_data;
    for ic = 1:5
        fprintf('%d, %d,\n', s, ic);
        [ca, cs] = spmo_cell_assignment_and_sequence(hw_data(:,1), hw_data(:,2:4), ico, ic);
        cellassign(ic).subject(s).hw_ca = ca;
        cellassign(ic).subject(s).hw_cs = cs;
        
        [ca, cs] = spmo_cell_assignment_and_sequence(eat_data(:,1), eat_data(:,2:4), ico, ic);
        cellassign(ic).subject(s).eat_ca = ca;
        cellassign(ic).subject(s).eat_cs = cs;        
    end
end

save('cellassign', 'cellassign');