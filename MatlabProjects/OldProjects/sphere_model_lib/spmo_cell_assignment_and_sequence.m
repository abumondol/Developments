function [cell_assignment, cell_sequence] = spmo_cell_assignment_and_sequence(t, data, ico, ico_number)
    data_count = length(data);
    ic = ico_number;
    cell_sequence = zeros(data_count, 7);            
    cell_assignment = zeros(data_count, 1);            

    vertices = ico(ic).vertices;
    all_nb = ico(ic).neighbors;
    min_rad = ico(ic).cosine_radius_min;            

    grepeat = repmat(data(1,:), length(vertices), 1);            
    d = 1 - sum(vertices.*grepeat, 2);            
    [~, v] = min(d);

    seq_count = 1;            
    last_k = 1;           

    for k = 2:data_count
        cell_assignment(k-1, 1) = v;
        d = 1 - sum(data(k,:).*vertices(v, :), 2);                
        if d <= min_rad(v)                    
            continue;
        end

        nb_indices = all_nb{v};
        grepeat = repmat(data(k, :), length(nb_indices), 1);
        nb_d = 1 - sum(vertices(nb_indices,:).* grepeat,2);                
        [mn_nb_d, min_ind] = min(nb_d);

        if d <= mn_nb_d
            continue;
        end

        cell_sequence(seq_count, 1) = v; 
        cell_sequence(seq_count, 2) = last_k; % start index in this cell
        cell_sequence(seq_count, 3) = k - 1; % end index in this cell
        cell_sequence(seq_count, 4) = t(last_k); % start time in this cell
        cell_sequence(seq_count, 5) = t(k); % end time in this cell
        cell_sequence(seq_count, 6) = k - last_k; % total points in this cell
        cell_sequence(seq_count, 7) = t(k) - t(last_k); % total duration in this cell

        seq_count = seq_count + 1;
        last_k = k;

        flag = false;
        while flag == false 
            v = nb_indices(min_ind);                     
            d = mn_nb_d; 
            if d <= min_rad(v)
                break;
            end

            nb_indices = all_nb{v};
            grepeat = repmat(data(k, :), length(nb_indices), 1);
            nb_d = 1 - sum(vertices(nb_indices,:).*grepeat, 2);                    
            [mn_nb_d, min_ind] = min(nb_d);

            if d <= mn_nb_d                        
                    break;
            end

            cell_sequence(seq_count, 1) = v;
            cell_sequence(seq_count, 2) = k;
            cell_sequence(seq_count, 3) = k;
            cell_sequence(seq_count, 4) = t(k);
            cell_sequence(seq_count, 5) = t(k);
            cell_sequence(seq_count, 6) = 1; % total points in this cell
            cell_sequence(seq_count, 7) = 0; % total duration in this cell
            seq_count = seq_count + 1;                                        
        end

    end

    cell_assignment(data_count, 1) = v;
    cell_sequence(seq_count, 1) = v;
    cell_sequence(seq_count, 2) = last_k;
    cell_sequence(seq_count, 3) = data_count;
    cell_sequence(seq_count, 4) = t(last_k);
    cell_sequence(seq_count, 5) = t(data_count);
    cell_sequence(seq_count, 6) = data_count - last_k + 1; % total points in this cell
    cell_sequence(seq_count, 7) = t(data_count) - t(last_k) + t(data_count) - t(data_count-1); % total duration in this cell

    cell_sequence = cell_sequence(1:seq_count, :);   
end        

