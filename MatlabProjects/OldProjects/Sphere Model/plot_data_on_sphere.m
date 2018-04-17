data = realdisp_quat;
sub_count = length(data);
ico_number = 3;
close all;

pos = 1;
for act = 0:33
    for sub = 10:10%sub_count
        sess_count = length(data(sub).session);
        for sess = 1:1%sess_count
            t = data(sub).session(sess).t;            
            chunks = realdisp_activity_chunks(sub).session(sess).chunks;
            c = chunks(chunks(:,1)==act, :);
            if isempty(c)
                continue
            end            
            
            grav = data(sub).session(sess).position(pos).Rz;            
            clen = size(c, 1);
            d = [];
            for i=1:clen
                c1 = c(i,2);
                c2 = c(i,3);
                d = [d; grav(c1:c2, :)];
                %tm = t(c1:c2, :);
                %[cell_assignment, cell_sequence] = cell_assignment_and_sequence(t, d, ico, ico_number);
                %summary = cell_sequence_summary(cell_sequence);
            end
            
            title = strcat('Subject:', num2str(sub), ', Session:', num2str(sess), ', Position:', num2str(pos), ', Activity:', num2str(act));
            fprintf('%s :: %d\n',title, length(d));
            %print_cell_sequence(cell_sequence); 
            %summary
           
            title = strcat('Subject:', num2str(sub), ', Session:', num2str(sess), ', Position:', num2str(pos), ', Activity:', num2str(act));
            plot_data_on_sphere_one_chunk(d, ico, ico_number, title);           
            %waitforbuttonpress
        end
    end
end